package com.deep.escqrs.product.domain.value_objects

@JvmInline
value class Price(val value: Int) {
    init {
        require(value >= 0) { "Price must not be negative" }
    }
}
