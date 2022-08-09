package com.deep.escqrs.product.command.infra

import com.deep.escqrs.core.EventStore
import com.deep.escqrs.product.domain.Product
import com.deep.escqrs.product.domain.ProductRepository
import com.deep.escqrs.shared.infra.BaseRepository
import org.springframework.stereotype.Component

@Component
class ProductRepositoryImpl (evenStore: EventStore): ProductRepository, BaseRepository<Product>(Product::class, evenStore)
