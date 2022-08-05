package com.deep.escqrs.product

import AggregateIdType
import AggregateRoot
import IRepository
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


class Repository<A: AggregateRoot>(
    private val type: KClass<A>,
    private val eventStore: EventStore
): IRepository<A> {
    companion object {
        inline operator fun <reified A : AggregateRoot> invoke(
            eventStore: EventStore
        ) = Repository(A::class, eventStore)
    }
    override fun save(aggregate: A, expectedVersions: Int) {
        eventStore.saveEvents(aggregate.id, aggregate.getUncommittedChanges(),expectedVersions)
    }

    override fun getById(aggregateId: AggregateIdType): A {
        val events = eventStore.getEventsForAggregate(aggregateId)
        return type.primaryConstructor!!.call(aggregateId, events)
    }
}
