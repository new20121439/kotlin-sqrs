package com.deep.escqrs.order.facades

import com.deep.escqrs.order.command.app.CreateOrder
import com.deep.escqrs.order.command.app.OrderCommandHandler
import com.deep.escqrs.order.facades.dto.CreateOrderDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class CommandController(
    @Autowired val commandHandler: OrderCommandHandler
) {

    @PostMapping("/orders")
    fun create(@RequestBody body: CreateOrderDto): String {
        return try {
            val createOrder = CreateOrder(UUID.randomUUID(), body.productItems, body.address)
            commandHandler.handle(createOrder)
            "OK"
        } catch (e: Exception) {
            println("Error $e")
            "Fail"
        }
    }
}
