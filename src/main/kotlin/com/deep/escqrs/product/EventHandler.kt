package com.deep.escqrs.product

import com.deep.escqrs.core.EventHandler


class ProductEventHandler (
    private val productRepository: ProductRepository
): EventHandler<ProductCreated>
{
    override fun handle(event: ProductCreated) {
        productRepository.save(ProductEntity(event.id, event.name, event.price))
    }
}
