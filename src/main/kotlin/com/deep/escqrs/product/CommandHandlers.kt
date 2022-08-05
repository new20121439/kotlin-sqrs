package com.deep.escqrs.product

import IRepository

class ProductCommandHandler (
    private val repository: IRepository<Product>
){
    fun handle(command: CreateProduct) {
        val item = Product(command.id, command.name, command.price)
        repository.save(item, -1)
    }

    fun handle(command: ChangeProductPrice) {
        val item = repository.getById(command.id)
        item.changePrice(command.price)
        repository.save(item, command.originalVersion)
    }
}
