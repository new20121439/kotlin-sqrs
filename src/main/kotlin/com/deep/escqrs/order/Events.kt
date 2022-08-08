package com.deep.escqrs.order

import com.deep.escqrs.core.Event
import java.util.*

data class OrderCreated (
    override val id: UUID,
    val productItems: MutableList<ProductItem>,
    val address: String
): Event()

data class OrderNewProductsAdded(
    override val id: UUID,
    val newProductItems: MutableList<ProductItem>
): Event()

data class OrderProductsRemoved(
    override val id: UUID,
    val removedProductItems: MutableList<UUID>
): Event()
