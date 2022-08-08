package com.deep.escqrs

import com.deep.escqrs.order.*
import com.deep.escqrs.product.EventBus
import com.deep.escqrs.product.EventRepository
import com.deep.escqrs.product.Repository
import com.deep.escqrs.product.SqlEventStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
}
