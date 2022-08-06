package com.deep.escqrs.product

import com.deep.escqrs.core.AggregateIdType
import com.deep.escqrs.core.Event
import com.deep.escqrs.core.EventDescriptor
import com.deep.escqrs.core.EventStore
import org.springframework.data.jpa.repository.JpaRepository

class LocalEventStore (
    val store: HashMap<AggregateIdType, MutableList<EventDescriptor>> = HashMap()
): EventStore {
    override fun getEventsForAggregate(aggregateId: AggregateIdType): List<Event>{
        if (!store.containsKey(aggregateId)) {
            throw AggregateNotFoundException()
        }
        return store[aggregateId]
            ?.sortedByDescending { it.version }
            ?.map { it.data } ?: emptyList()
    }

    override fun saveEvents(aggregateId: AggregateIdType, events: List<Event>, expectedVersion: Int) {
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


interface EventRepository: JpaRepository<EventDescriptor, AggregateIdType> {
    fun findByAggregateIdOrderByVersionDesc(aggregateId: AggregateIdType): List<EventDescriptor>
}

class SqlEventStore(
    val eventRepository: EventRepository
) : EventStore {
    override fun getEventsForAggregate(aggregateId: AggregateIdType): List<Event> {
        val eventDescriptors = eventRepository.findByAggregateIdOrderByVersionDesc(aggregateId)
        return eventDescriptors.map { it.data }
    }

    override fun saveEvents(aggregateId: AggregateIdType, events: List<Event>, expectedVersion: Int) {
        var eventDescriptors = eventRepository.findByAggregateIdOrderByVersionDesc(aggregateId)
        if (!eventDescriptors.isEmpty()
            && eventDescriptors.last().version != expectedVersion
            && expectedVersion != -1
        ) {
            throw VersionMismatchException()
        }
        var i = expectedVersion
        events.forEach {
            i++
            eventRepository.save(EventDescriptor(null, aggregateId, it, i))
        }
    }

}

class VersionMismatchException (
    message: String = "Version of aggregate is mismatch with latest event version"
): Exception(message)

class AggregateNotFoundException(
    message: String = "Aggregate is not found"
): Exception(message)
