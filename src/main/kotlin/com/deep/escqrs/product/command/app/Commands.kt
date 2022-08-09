package com.deep.escqrs.product.command.app

import com.deep.escqrs.core.Command
import com.deep.escqrs.product.domain.value_objects.Price
import java.util.*

data class CreateProduct (
    override val id: UUID,
    var name: String,
    var price: Price
): Command()

data class ChangeProductPrice (
    override val id: UUID,
    var price: Price,
    val originalVersion: Int
): Command()
