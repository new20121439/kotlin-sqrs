package com.deep.escqrs.product

import AggregateIdType
import com.deep.escqrs.core.Event


class EventStore (
    private val store: HashMap<AggregateIdType, MutableList<EventDescriptor>> = HashMap()
){
    data class EventDescriptor (
        val id: AggregateIdType,
        val data: Event,
        val version: Int
    )

    fun getEventsForAggregate(aggregateId: AggregateIdType): List<Event>{
        if (!store.containsKey(aggregateId)) {
            throw AggregateNotFoundException()
        }
        return store[aggregateId]
            ?.sortedByDescending { it.version }
            ?.map { it.data } ?: emptyList()
    }

    fun saveEvents(aggregateId: AggregateIdType, events: List<Event>, expectedVersion: Int) {
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
            store[aggregateId]?.add(EventDescriptor(aggregateId, it, i))
        }
    }

    fun printStore() {
        store.forEach(System.out::println)
    }
}

class VersionMismatchException (
    message: String = "Version of aggregate is mismatch with latest event version"
): Exception(message)

class AggregateNotFoundException(
    message: String = "Aggregate is not found"
): Exception(message)
