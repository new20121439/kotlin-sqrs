package com.deep.escqrs.order.command.app

import com.deep.escqrs.core.Command
import com.deep.escqrs.order.domain.value_objects.ProductItem
import java.util.*

data class CreateOrder(
    override val id: UUID,
    val productItems: MutableList<ProductItem>,
    val address: String
): Command()

data class OrderAddNewProducts(
    override val id: UUID,
    val newProductItems: MutableList<ProductItem>,
    val originalVersion: Int
): Command() {
}

data class OrderRemoveProducts(
    override val id: UUID,
    val removedProductItems: MutableList<UUID>,
    val originalVersion: Int
): Command()
