package com.deep.escqrs.core

abstract class Event : Message()

data class EventDescriptor (
    val id: AggregateIdType,
    val data: Event,
    val version: Int
)
