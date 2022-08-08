package com.deep.escqrs.order.domain

import com.deep.escqrs.core.AggregateRoot
import com.deep.escqrs.core.Event
import java.util.*

class Order (
    id: UUID,
    events: List<Event> = emptyList()
): AggregateRoot(id, events) {
    constructor(
        id: UUID,
        productItems: MutableList<ProductItem>,
        address: String
    ) : this(id) {
        require(productItems.isNotEmpty()) { "Product items must not be empty" }
        require(address.isNotEmpty()) { "Address must not be empty" }
        applyChange(OrderCreated(id, productItems, address))
    }

    fun addNewProducts(newProducts: MutableList<ProductItem>) {
        require(newProducts.isNotEmpty()) { "New Product items must not be empty" }
        applyChange(OrderNewProductsAdded(id, newProducts))
    }

    fun removeProducts(removeProducts: MutableList<UUID>) {
        require(removeProducts.isNotEmpty()) { "Removed product list must not be empty" }
        applyChange(OrderProductsRemoved(id, removeProducts))
    }
}
