package com.deep.escqrs

import com.deep.escqrs.product.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class Product2Tests(
    @Autowired val eventRepository: EventRepository
) {

    @Test
    fun `A product should be created in SQL`() {
        // Arrange
        val eventStore = SqlEventStore(eventRepository)
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
}
