package com.deep.escqrs.core

import java.util.*

interface EventStore {
    fun getEventsForAggregate(aggregateId: UUID): List<Event>
    fun saveEvents(aggregateId: UUID, events: List<Event>, expectedVersion: Int)
}
