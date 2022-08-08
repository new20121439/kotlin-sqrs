package com.deep.escqrs.order

import com.deep.escqrs.core.IRepository

class OrderCommandHandler (
    private val repo: IRepository<Order>
) {
    fun handle(command: CreateOrder) {
        val order = Order(command.id, command.productItems, command.address)
        repo.save(order, -1)
    }

     fun handle(command: OrderAddNewProducts) {
         val order = repo.getById(command.id)
         order.addNewProducts(command.newProductItems)
         repo.save(order, command.originalVersion)
    }

    fun handle(command: OrderRemoveProducts) {
        val order = repo.getById(command.id)
        order.removeProducts(command.removedProductItems)
        repo.save(order, command.originalVersion)
    }
}
