package com.deep.escqrs.product.query

import java.util.*

interface ReadModelFacade {
    fun getProducts(): List<ProductDto>
    fun getProductById(id: UUID): ProductDto
}

data class ProductDto (
    val id: UUID,
    val name: String,
    val price: Int
)


