package com.deep.escqrs.product

import AggregateIdType
import AggregateRoot
import com.deep.escqrs.core.Event


class Product (
    id: AggregateIdType,
    events: List<Event> = emptyList()
): AggregateRoot(id, events) {
    constructor(
        id: AggregateIdType,
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