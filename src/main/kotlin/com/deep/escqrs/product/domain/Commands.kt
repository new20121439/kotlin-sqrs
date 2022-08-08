package com.deep.escqrs.product.domain

import com.deep.escqrs.core.Command
import java.util.*

data class CreateProduct (
    override val id: UUID,
    var name: String,
    var price: Int
): Command()

data class ChangeProductPrice (
    override val id: UUID,
    var price: Int,
    val originalVersion: Int
): Command()
