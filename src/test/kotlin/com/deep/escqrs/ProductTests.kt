package com.deep.escqrs

import com.deep.escqrs.product.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductTests(
    @Autowired val eventRepository: EventRepository,
    @Autowired val productRepository: ProductRepository
) {

    @Test
    fun `A product should be created in SQL`() {
        // Arrange
        val eventBus = EventBus()
        val eventStore = SqlEventStore(eventRepository, eventBus)
        val repo = Repository(Product::class, eventStore)
        val productCommandHandler = ProductCommandHandler(repo)
        val productEventHandler = ProductEventHandler(productRepository)
        eventBus.register(productEventHandler)
        // Act
        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", 100)
        productCommandHandler.handle(createProduct)

        // Assert
        assertEquals(
            listOf(ProductCreated(uuid, "Bicycle", 100)),
            eventStore.getEventsForAggregate(uuid)
        )
        assertEquals(
            ProductEntity(createProduct.id, createProduct.name, createProduct.price),
            productRepository.findById(uuid).get()
        )
    }

    @Test
    fun `Should throw ProductIsAlreadyExisted when id is valid in db`() {
        // Arrange
        val eventBus = EventBus()
        val eventStore = SqlEventStore(eventRepository, eventBus)
        val repo = Repository(Product::class, eventStore)
        val productCommandHandler = ProductCommandHandler(repo)
        val productEventHandler = ProductEventHandler(productRepository)
        eventBus.register(productEventHandler)
        // Act
        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", 100)
        productCommandHandler.handle(createProduct)

        // Assert
        assertThrows<ProductIsAlreadyExisted>{
            productCommandHandler.handle(createProduct)
        }
    }

    @Test
    fun `Product price should be changed`() {
        // Arrange
        val eventBus = EventBus()
        val eventStore = SqlEventStore(eventRepository, eventBus)
        val repo = Repository(Product::class, eventStore)
        val productCommandHandler = ProductCommandHandler(repo)

        // Act
        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", 100)
        productCommandHandler.handle(createProduct)
        val priceChange = ChangeProductPrice(uuid, 200, 0)
        productCommandHandler.handle(priceChange)

        // Assert
        assertEquals(
            listOf(
                ProductPriceChanged(uuid, 200),
                ProductCreated(uuid, "Bicycle", 100)
            ),
            eventStore.getEventsForAggregate(uuid)
        )
    }
}
