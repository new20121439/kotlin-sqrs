package com.deep.escqrs.product.query

import com.deep.escqrs.core.EventHandler
import com.deep.escqrs.product.command.domain.ProductCreated


class ProductEventHandler (
    private val productRepository: ProductRepository
): EventHandler<ProductCreated>
{
    override fun handle(event: ProductCreated) {
        productRepository.save(ProductEntity(event.id, event.name, event.price))
    }
}
