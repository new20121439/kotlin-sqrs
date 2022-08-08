package com.deep.escqrs

import com.deep.escqrs.shared.EventRepository
import com.deep.escqrs.product.domain.ProductCommandHandler
import com.deep.escqrs.product.domain.ProductIsAlreadyExisted
import com.deep.escqrs.product.domain.*
import com.deep.escqrs.product.domain.value_objects.Price
import com.deep.escqrs.shared.SqlEventStore
import com.deep.escqrs.product.read_model.ProductEntity
import com.deep.escqrs.product.read_model.ProductEventHandler
import com.deep.escqrs.product.read_model.ProductRepository
import com.deep.escqrs.shared.EventBus
import com.deep.escqrs.shared.Repository
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
    @Autowired val productRepository: ProductRepository
) {
    private lateinit var eventBus: EventBus
    private lateinit var eventStore: SqlEventStore
    private lateinit var repo: Repository<Product>
    private lateinit var productCommandHandler: ProductCommandHandler
    private lateinit var productEventHandler: ProductEventHandler

    @BeforeEach
    fun beforeEach() {
        eventBus = EventBus()
        eventStore = SqlEventStore(eventRepository, eventBus)
        repo = Repository(Product::class, eventStore)
        productCommandHandler = ProductCommandHandler(repo)
        productEventHandler = ProductEventHandler(productRepository)
    }

    @Test
    fun `A product should be created in SQL`() {
        // Arrange
        eventBus.register(productEventHandler)
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
            productRepository.findById(uuid).get()
        )
    }

    @Test
    fun `Should throw ProductIsAlreadyExisted when id is valid in db`() {
        // Arrange
        eventBus.register(productEventHandler)
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
