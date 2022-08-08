package com.deep.escqrs.order.command.domain.value_objects

import java.util.*

data class ProductItem (
    val id: UUID,
    val quantity: Int
) {
    init {
        require(quantity > 0) { "Quantity must be a positive integer" }
    }
}
