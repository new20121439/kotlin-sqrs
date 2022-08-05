package com.deep.escqrs

import com.deep.escqrs.core.EventDescriptor
import com.deep.escqrs.product.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ProductTests {

    @Test
    fun `A product should be created`() {
        // Arrange
        val eventStore = EventStore()
        val repo = Repository(Product::class, eventStore)
        val productCommandHandler = ProductCommandHandler(repo)

        // Act
        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", 100)
        productCommandHandler.handle(createProduct)

        // Assert
        assertEquals(
            listOf(ProductCreated(uuid, "Bicycle", 100)),
            eventStore.getEventsForAggregate(uuid)
        )
    }

    @Test
    fun `Product price should be changed`() {
        // Arrange
        val eventStore = EventStore()
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
