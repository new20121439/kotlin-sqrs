package com.deep.escqrs.order

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
        require(productItems.isNotEmpty()) { ProductListIsEmpty() }
        require(address.isNotEmpty()) { AddressIsEmpty() }
        applyChange(OrderCreated(id, productItems, address))
    }

    fun addNewProducts(newProducts: MutableList<ProductItem>) {
        require(newProducts.isNotEmpty()) { ProductListIsEmpty() }
        applyChange(OrderNewProductsAdded(id, newProducts))
    }

    fun removeProducts(removeProducts: MutableList<UUID>) {
        require(removeProducts.isNotEmpty()) { ProductListIsEmpty()}
        applyChange(OrderProductsRemoved(id, removeProducts))
    }
}

class AddressIsEmpty: Exception() {
}

class ProductListIsEmpty : Exception() {
}

class ProductIsAlreadyExisted() : Exception() {}

class ProductItem(
    val id: UUID,
    val quantity: Int
)
