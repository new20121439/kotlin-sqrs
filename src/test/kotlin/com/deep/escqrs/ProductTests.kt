package com.deep.escqrs

import com.deep.escqrs.product.command.app.ChangeProductPrice
import com.deep.escqrs.product.command.app.CreateProduct
import com.deep.escqrs.shared.infra.EventRepository
import com.deep.escqrs.product.command.app.ProductCommandHandler
import com.deep.escqrs.product.command.app.ProductIsAlreadyExisted
import com.deep.escqrs.product.command.infra.ProductRepositoryImpl
import com.deep.escqrs.product.domain.ProductCreated
import com.deep.escqrs.product.domain.ProductPriceChanged
import com.deep.escqrs.product.domain.value_objects.Price
import com.deep.escqrs.shared.infra.SqlEventStore
import com.deep.escqrs.product.query.ProductEntity
import com.deep.escqrs.product.query.ProductEventHandler
import com.deep.escqrs.product.query.ProductRepository
import com.deep.escqrs.shared.infra.EventBus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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
    @Autowired val productReadRepository: ProductRepository
) {
    private lateinit var eventBus: EventBus
    private lateinit var eventStore: SqlEventStore
    private lateinit var repo: com.deep.escqrs.product.domain.ProductRepository
    private lateinit var productCommandHandler: ProductCommandHandler
    private lateinit var productEventHandler: ProductEventHandler

    @BeforeEach
    fun beforeEach() {
        eventBus = EventBus()
        eventStore = SqlEventStore(eventRepository, eventBus)
        repo = ProductRepositoryImpl(eventStore)
        productCommandHandler = ProductCommandHandler(repo)
        productEventHandler = ProductEventHandler(productReadRepository)
    }

    @Test
    fun `A product should be created in DB`() {
        // Arrange
        eventBus.register(ProductCreated::class.java.typeName, productEventHandler)
        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", Price(100))

        // Act
        productCommandHandler.handle(createProduct)

        // Assert
        assertEquals(
            listOf(ProductCreated(uuid, "Bicycle", 100)),
            eventStore.getEventsForAggregate(uuid)
        )
        assertEquals(
            ProductEntity(createProduct.id, createProduct.name, createProduct.price.value),
            productReadRepository.findById(uuid).get()
        )
    }

    @Test
    fun `Should throw ProductIsAlreadyExisted when id is valid in db`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", Price(100))

        // Act
        productCommandHandler.handle(createProduct)

        // Assert
        assertThrows<ProductIsAlreadyExisted>{
            productCommandHandler.handle(createProduct)
        }
    }

    @Test
    fun `Product price should be changed`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", Price(100))
        productCommandHandler.handle(createProduct)
        val priceChange = ChangeProductPrice(uuid, Price(200), 0)

        // Act
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

    @Test
    fun `Throw when price is negative`() {
        // Arrange
        // Act
        // Assert
        val exception = assertThrows<IllegalArgumentException> {
            Price(-1)
        }
        assertEquals("Price must not be negative", exception.message)
    }
}
