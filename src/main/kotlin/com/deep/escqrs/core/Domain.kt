package com.deep.escqrs.core

import java.util.*

abstract class AggregateRoot(
    val id: UUID,
    val events: List<Event>
) {
    private var changes: MutableList<Event> = mutableListOf()
    fun applyChange(event: Event) {
        changes.add(event)
    }

    fun getUncommittedChanges(): List<Event> {
        return changes
    }

    fun markChangesAsCommitted() {
        changes.clear()
    }
}

interface IRepository<A: AggregateRoot> {
    fun save(aggregate: A, expectedVersions: Int)
    fun getById(aggregateId: UUID): A
}
