package com.deep.escqrs.product.command.domain

import com.deep.escqrs.core.AggregateRoot
import com.deep.escqrs.core.Event
import com.deep.escqrs.product.command.domain.value_objects.Price
import java.util.*


class Product (
    id: UUID,
    events: List<Event> = emptyList()
): AggregateRoot(id, events) {
    constructor(
        id: UUID,
        name: String,
        price: Price
    ): this(id) {
        require(name.isNotEmpty()) { "Name must not be empty" }
        val createdProduct = ProductCreated(id, name, price.value)
        applyChange(createdProduct)
    }

    fun changePrice(newPrice: Price) {
        var latestPrice: Int? = null
        for (item in events) {
            latestPrice = when(item) {
                is ProductCreated -> item.price
                is ProductPriceChanged -> item.price
                else -> null
            }
            if (latestPrice != null) break
        }
        if (latestPrice != null  && latestPrice == newPrice.value) throw ArgumentException("New price is already set")
        applyChange(ProductPriceChanged(id, newPrice.value))
    }
}
