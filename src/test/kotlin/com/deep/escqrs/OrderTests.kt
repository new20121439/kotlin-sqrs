package com.deep.escqrs

import com.deep.escqrs.core.EventStore
import com.deep.escqrs.order.domain.*
import com.deep.escqrs.shared.EventRepository
import com.deep.escqrs.shared.SqlEventStore
import com.deep.escqrs.shared.EventBus
import com.deep.escqrs.shared.Repository
import org.junit.jupiter.api.AfterEach
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
class OrderTests (
    @Autowired private val eventRepository: EventRepository,
){
    private lateinit var eventBus: EventBus
    private lateinit var eventStore: EventStore
    private lateinit var repo: Repository<Order>
    private lateinit var commandHandler: OrderCommandHandler

    @BeforeEach
    fun beforeEach() {
        eventBus = EventBus()
        eventStore = SqlEventStore(eventRepository, eventBus)
        repo = Repository(Order::class, eventStore)
        commandHandler = OrderCommandHandler(repo)
    }

    @Test
    fun `Order should be created`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val address = "Ha Noi"
        val productItems = mutableListOf<ProductItem>(
            ProductItem(UUID.randomUUID(), 1),
            ProductItem(UUID.randomUUID(), 2)
        )
        val createOrder = CreateOrder(uuid, productItems, address)

        // Act
        commandHandler.handle(createOrder)

        // Assert
        assertEquals(
            listOf(OrderCreated(uuid, productItems, address)),
            eventStore.getEventsForAggregate(uuid)
        )
    }

    @Test
    fun `Order throws OrderIsAlreadyExisted When order Id is existed`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val address = "Ha Noi"
        val productItems = mutableListOf<ProductItem>(
            ProductItem(UUID.randomUUID(), 1),
            ProductItem(UUID.randomUUID(), 2)
        )
        val createOrder = CreateOrder(uuid, productItems, address)
        commandHandler.handle(createOrder)

        // Act
        // Assert
        assertThrows<OrderIsAlreadyExisted> {
            commandHandler.handle(createOrder)
        }
    }

    @Test
    fun `Order adds new products`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val address = "Ha Noi"
        val productItems = mutableListOf<ProductItem>(
            ProductItem(UUID.randomUUID(), 1),
            ProductItem(UUID.randomUUID(), 2)
        )
        val createOrder = CreateOrder(uuid, productItems, address)
        commandHandler.handle(createOrder)

        val newProductItems = mutableListOf<ProductItem>(
            ProductItem(UUID.randomUUID(), 3)
        )
        val addNewProducts = OrderAddNewProducts(uuid, newProductItems, 0)

        // Act
        commandHandler.handle(addNewProducts)

        // Assert
        assertEquals(
            listOf(
                OrderNewProductsAdded(uuid, newProductItems),
                OrderCreated(uuid, productItems, address)
            ),
            eventStore.getEventsForAggregate(uuid)
        )
    }

    @Test
    fun `Throws when quantity of product is not positive`() {
        // Arrange
        // Act
        // Assert
        val exception = assertThrows<IllegalArgumentException>{
            ProductItem(UUID.randomUUID(), 0)
        }
        assertEquals("Quantity must be a positive integer", exception.message)
    }

    @Test
    fun `Throws when ProductItem is empty`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val createOrder = CreateOrder(uuid, mutableListOf(), "Ha Noi")

        // Act
        // Assert
        val exception = assertThrows<IllegalArgumentException> {
            commandHandler.handle(createOrder)
        }
        assertEquals("Product items must not be empty", exception.message)
    }

    @Test
    fun `Throws when Address is empty`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val productItems = mutableListOf<ProductItem>(
            ProductItem(UUID.randomUUID(), 1),
            ProductItem(UUID.randomUUID(), 2)
        )
        val createOrder = CreateOrder(uuid, productItems, "")
        // Act
        // Assert
        val exception = assertThrows<IllegalArgumentException> {
            commandHandler.handle(createOrder)
        }
        assertEquals("Address must not be empty", exception.message)
    }
}
