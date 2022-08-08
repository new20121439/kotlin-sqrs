package com.deep.escqrs

import com.deep.escqrs.order.*
import com.deep.escqrs.shared.EventRepository
import com.deep.escqrs.shared.SqlEventStore
import com.deep.escqrs.shared.EventBus
import com.deep.escqrs.shared.Repository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderTests (
    @Autowired val eventRepository: EventRepository,
){

    @Test
    fun `Order should be created`() {
        // Arrange
        val eventBus = EventBus()
        val eventStore = SqlEventStore(eventRepository, eventBus)
        val repo = Repository(Order::class, eventStore)
        val commandHandler = OrderCommandHandler(repo)
        // Act
        val uuid = UUID.randomUUID()
        val address = "Ha Noi"
        val productItems = mutableListOf<ProductItem>(
            ProductItem(UUID.randomUUID(), 1),
            ProductItem(UUID.randomUUID(), 2)
        )
        val createOrder = CreateOrder(uuid, productItems, address)
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
        val eventBus = EventBus()
        val eventStore = SqlEventStore(eventRepository, eventBus)
        val repo = Repository(Order::class, eventStore)
        val commandHandler = OrderCommandHandler(repo)
        // Act
        val uuid = UUID.randomUUID()
        val address = "Ha Noi"
        val productItems = mutableListOf<ProductItem>(
            ProductItem(UUID.randomUUID(), 1),
            ProductItem(UUID.randomUUID(), 2)
        )
        val createOrder = CreateOrder(uuid, productItems, address)
        commandHandler.handle(createOrder)

        // Assert
        assertThrows<OrderIsAlreadyExisted> {
            commandHandler.handle(createOrder)
        }
    }

    @Test
    fun `Order adds new products`() {
        // Arrange
        val eventBus = EventBus()
        val eventStore = SqlEventStore(eventRepository, eventBus)
        val repo = Repository(Order::class, eventStore)
        val commandHandler = OrderCommandHandler(repo)
        // Act
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
    fun `Throws QuantityIsNotPositiveNumber when quantity of product is not positive`() {
        // Arrange
        // Act
        // Assert
        assertThrows<QuantityIsNotPositiveNumber>{
            ProductItem(UUID.randomUUID(), 0)
        }
    }
}
