package com.deep.escqrs.product.command.app

import com.deep.escqrs.product.domain.Product
import com.deep.escqrs.product.domain.ProductRepository

class ProductCommandHandler (
    private val repo: ProductRepository
){
    fun handle(command: CreateProduct) {
        val foundProduct = repo.getById(command.id)
        if (foundProduct.events.isNotEmpty()) {
            throw ProductIsAlreadyExisted()
        }
        val item = Product(command.id, command.name, command.price)
        repo.save(item, -1)
    }

    fun handle(command: ChangeProductPrice) {
        val item = repo.getById(command.id)
        item.changePrice(command.price)
        repo.save(item, command.originalVersion)
    }
}

class ProductIsAlreadyExisted() : Exception() {
}
