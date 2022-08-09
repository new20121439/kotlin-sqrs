package com.deep.escqrs.shared.infra

import com.deep.escqrs.core.AggregateRoot
import com.deep.escqrs.core.EventStore
import com.deep.escqrs.core.Repository
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


open class BaseRepository<A: AggregateRoot>(
    private val type: KClass<A>,
    private val eventStore: EventStore
): Repository<A> {
    companion object {
        inline operator fun <reified A : AggregateRoot> invoke(
            eventStore: EventStore
        ) = BaseRepository(A::class, eventStore)
    }
    override fun save(aggregate: A, expectedVersions: Int) {
        eventStore.saveEvents(aggregate.id, aggregate.getUncommittedChanges(),expectedVersions)
    }

    override fun getById(aggregateId: UUID): A {
        val events = eventStore.getEventsForAggregate(aggregateId)
        return type.primaryConstructor!!.call(aggregateId, events)
    }
}
