package com.deep.escqrs.product

import AggregateIdType
import com.deep.escqrs.core.Command


data class CreateProduct (
    override val id: AggregateIdType,
    var name: String,
    var price: Int
): Command()

data class ChangeProductPrice (
    override val id: AggregateIdType,
    var price: Int,
    val originalVersion: Int
): Command()
