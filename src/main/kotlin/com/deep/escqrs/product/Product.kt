package com.deep.escqrs.product

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "products")
data class ProductEntity (
    @Id
    val id: UUID,

    @Column(nullable = false)
    val name: String,

    @Column()
    val price: Int
)

interface ProductRepository: JpaRepository<ProductEntity, UUID>
