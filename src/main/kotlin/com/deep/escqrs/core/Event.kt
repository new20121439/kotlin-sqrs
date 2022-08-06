package com.deep.escqrs.core

import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

abstract class Event : Message()

@Entity
data class EventDescriptor (
    @Id
    val id: AggregateIdType,

    @Type(type = "json")
    @Column(nullable = false)
    val data: Event,

    @Column(nullable = false)
    val version: Int
)
