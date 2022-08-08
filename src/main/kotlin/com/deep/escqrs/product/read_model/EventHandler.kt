package com.deep.escqrs.product.read_model

import com.deep.escqrs.core.EventHandler
import com.deep.escqrs.product.domain.ProductCreated


class ProductEventHandler (
    private val productRepository: ProductRepository
): EventHandler<ProductCreated>
{
    override fun handle(event: ProductCreated) {
        productRepository.save(ProductEntity(event.id, event.name, event.price))
    }
}
