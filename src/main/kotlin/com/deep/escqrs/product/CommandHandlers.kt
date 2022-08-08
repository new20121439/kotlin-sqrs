package com.deep.escqrs.product

import com.deep.escqrs.core.IRepository

class ProductCommandHandler (
    private val repo: IRepository<Product>
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
