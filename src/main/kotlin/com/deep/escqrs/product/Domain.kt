package com.deep.escqrs.product

import com.deep.escqrs.core.AggregateRoot
import com.deep.escqrs.core.Event
import java.util.*


class Product (
    id: UUID,
    events: List<Event> = emptyList()
): AggregateRoot(id, events) {
    constructor(
        id: UUID,
        name: String,
        price: Int
    ): this(id) {
        val createdProduct = ProductCreated(id, name, price)
        applyChange(createdProduct)
    }

    fun changePrice(newPrice: Int) {
        if (newPrice < 0) throw ArgumentException("Price must not be negative")
        var latestPrice: Int? = null
        for (item in events) {
            latestPrice = when(item) {
                is ProductCreated -> item.price
                is ProductPriceChanged -> item.price
                else -> null
            }
            if (latestPrice != null) break
        }
        if (latestPrice != null  && latestPrice == newPrice) throw ArgumentException("New price is already set")
        applyChange(ProductPriceChanged(id, newPrice))
    }
}
