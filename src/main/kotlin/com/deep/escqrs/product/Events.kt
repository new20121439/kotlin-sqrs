package com.deep.escqrs.product

import com.deep.escqrs.core.Event
import java.util.*

data class ProductCreated(
    override val id: UUID,
    val name: String,
    val price: Int
): Event()

data class ProductPriceChanged (
    override val id: UUID,
    val price: Int
): Event()

class ArgumentException(message: String) : Exception(message)
