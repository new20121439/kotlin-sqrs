package com.deep.escqrs.product.domain

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
        require(name.isNotEmpty()) { "Name must not be empty" }
        require(price >= 0) { "Price must not be negative" }
        val createdProduct = ProductCreated(id, name, price)
        applyChange(createdProduct)
    }

    fun changePrice(newPrice: Int) {
        require(newPrice >= 0) { "Price must not be negative" }
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
