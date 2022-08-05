package com.deep.escqrs

import com.deep.escqrs.product.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ProductTests {
    @Test
    fun `A product should be created`() {
        val eventStore = EventStore()
        val repo = Repository<Product>(Product::class, eventStore)
        val productCommandHandler = ProductCommandHandler(repo)

        val uuid = UUID.randomUUID()
        val createProduct = CreateProduct(uuid,"Bicycle", 100)
        productCommandHandler.handle(createProduct)

        val changePrice = ChangeProductPrice(uuid, 10, 0)
        productCommandHandler.handle(changePrice)

        val changePrice2 = ChangeProductPrice(uuid, 20, 1)
        productCommandHandler.handle(changePrice2)

        eventStore.printStore()
    }
}
