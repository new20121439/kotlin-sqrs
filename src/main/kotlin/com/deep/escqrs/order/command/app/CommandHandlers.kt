package com.deep.escqrs.order.command.app

import com.deep.escqrs.order.domain.Order
import com.deep.escqrs.order.domain.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class OrderCommandHandler (
    @Autowired private val repo: OrderRepository
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
