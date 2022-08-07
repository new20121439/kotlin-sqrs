package com.deep.escqrs.product

import com.deep.escqrs.core.Event
import com.deep.escqrs.core.EventHandler

class EventBus: Publisher {
    private val topics = mutableMapOf<String, MutableList<EventHandler<Event>>>()

    override fun publish(event: Event) {
        val typeName = event::class.java.typeName
        if (!topics.containsKey(typeName)) {
            println("Don't have any handler for event $typeName")
            return
        } else {
            topics[typeName]?.forEach {
                try { it.handle(event) }
                catch (e: Exception) { println("Failed to handle event $typeName: \n $e") }
            }
        }
    }

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline fun <reified T: Event> register(handler: EventHandler<T>) {
        val typeName = T::class.java.typeName
        val eventHandlers = topics[typeName] ?: mutableListOf()
        eventHandlers.add(handler as EventHandler<Event>)
        topics[typeName] = eventHandlers
    }
}