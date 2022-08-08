package com.deep.escqrs.order.domain

import com.deep.escqrs.core.IRepository

class OrderCommandHandler (
    private val repo: IRepository<Order>
) {
    fun handle(command: CreateOrder) {
        val foundOrder = repo.getById(command.id)
        if (foundOrder.events.isNotEmpty()) {
            throw OrderIsAlreadyExisted()
        }
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

class OrderIsAlreadyExisted(): Exception() {

}
