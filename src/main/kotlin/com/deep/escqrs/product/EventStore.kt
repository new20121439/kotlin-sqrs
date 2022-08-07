package com.deep.escqrs.product

import com.deep.escqrs.core.Event
import com.deep.escqrs.core.EventDescriptor
import com.deep.escqrs.core.EventStore
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import kotlin.collections.HashMap

class LocalEventStore (
    val store: HashMap<UUID, MutableList<EventDescriptor>> = HashMap()
): EventStore {
    override fun getEventsForAggregate(aggregateId: UUID): List<Event>{
        if (!store.containsKey(aggregateId)) {
            throw AggregateNotFoundException()
        }
        return store[aggregateId]
            ?.sortedByDescending { it.version }
            ?.map { it.data } ?: emptyList()
    }

    override fun saveEvents(aggregateId: UUID, events: List<Event>, expectedVersion: Int) {
        var eventDescriptors: MutableList<EventDescriptor>  = mutableListOf()
        if (!store.containsKey(aggregateId)) {
            store[aggregateId] = eventDescriptors
        } else {
            eventDescriptors = store[aggregateId]!!
            if (eventDescriptors.last().version != expectedVersion && expectedVersion != -1) {
                throw VersionMismatchException()
            }
        }
        var i = expectedVersion
        events.forEach {
            i++
            store[aggregateId]?.add(EventDescriptor(null, aggregateId, it, i))
        }
    }
}


interface EventRepository: JpaRepository<EventDescriptor, UUID> {
    fun findByAggregateIdOrderByVersionDesc(aggregateId: UUID): List<EventDescriptor>
}

class SqlEventStore(
    private val eventRepository: EventRepository,
    private val publisher: Publisher
) : EventStore {
    override fun getEventsForAggregate(aggregateId: UUID): List<Event> {
        val eventDescriptors = eventRepository.findByAggregateIdOrderByVersionDesc(aggregateId)
        return eventDescriptors.map { it.data }
    }

    override fun saveEvents(aggregateId: UUID, events: List<Event>, expectedVersion: Int) {
        val eventDescriptors = eventRepository.findByAggregateIdOrderByVersionDesc(aggregateId)
        if (eventDescriptors.isNotEmpty()
            && eventDescriptors.last().version != expectedVersion
            && expectedVersion != -1
        ) {
            throw VersionMismatchException()
        }
        var i = expectedVersion
        events.forEach {
            i++
            eventRepository.save(EventDescriptor(null, aggregateId, it, i))
            publisher.publish(it)
        }
    }

}

interface Publisher {
    fun publish(event: Event)
}

class VersionMismatchException (
    message: String = "Version of aggregate is mismatch with latest event version"
): Exception(message)

class AggregateNotFoundException(
    message: String = "Aggregate is not found"
): Exception(message)
