package com.deep.escqrs.core

interface EventStore {
    fun getEventsForAggregate(aggregateId: AggregateIdType): List<Event>
    fun saveEvents(aggregateId: AggregateIdType, events: List<Event>, expectedVersion: Int)
}
