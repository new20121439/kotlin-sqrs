package com.deep.escqrs.order.command.infra

import com.deep.escqrs.core.EventStore
import com.deep.escqrs.order.domain.Order
import com.deep.escqrs.order.domain.OrderRepository
import com.deep.escqrs.shared.infra.BaseRepository
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl(evenStore: EventStore): OrderRepository, BaseRepository<Order>(Order::class, evenStore)
