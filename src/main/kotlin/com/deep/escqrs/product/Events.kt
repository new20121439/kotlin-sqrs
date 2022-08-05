package com.deep.escqrs.product

import AggregateIdType
import com.deep.escqrs.core.Event

data class ProductCreated(
    override val id: AggregateIdType,
    val name: String,
    val price: Int
): Event()
data class ProductPriceChanged (
    override val id: AggregateIdType,
    val price: Int
): Event()


class ArgumentException(message: String) : Exception(message)
