package com.deep.escqrs.order.facades.dto

import com.deep.escqrs.order.domain.value_objects.ProductItem

data class CreateOrderDto (
    val productItems: MutableList<ProductItem>,
    val address: String
) {
}
