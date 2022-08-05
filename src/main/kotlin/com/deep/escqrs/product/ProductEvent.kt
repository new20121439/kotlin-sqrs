package com.deep.escqrs.product

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


fun main(args: Array<String>) {
    val eventStore = EventStore()
    val repo = Repository<Product>(Product::class, eventStore)
    val productCommandHandler = ProductCommandHandler(repo)

    val uuid = UUID.randomUUID()
    val createProduct = CreateProduct(uuid,"Bicycle", 100)
    productCommandHandler.handle(createProduct)

    val changePrice = ChangeProductPrice(uuid, 10, 0)
    productCommandHandler.handle(changePrice)

    val changePrice2 = ChangeProductPrice(uuid, 10, 1)
    productCommandHandler.handle(changePrice2)

    eventStore.printStore()
}

class ProductCommandHandler (
    private val repository: IRepository<Product>
){
    fun handle(command: CreateProduct) {
        val item = Product(command.id, command.name, command.price)
        repository.save(item, -1)
    }

    fun handle(command: ChangeProductPrice) {
        val item = repository.getById(command.id)
        item.changePrice(command.price)
        repository.save(item, command.originalVersion)
    }
}

abstract class Message{
    abstract val id: UUID
}
abstract class Command : Message()
abstract class Event : Message()

data class ProductCreated(
    override val id: AggregateIdType,
    val name: String,
    val price: Int
): Event()

data class ProductPriceChanged (
    override val id: AggregateIdType,
    val price: Int
): Event()

typealias AggregateIdType = UUID

data class CreateProduct (
    override val id: AggregateIdType,
    var name: String,
    var price: Int
): Command()

data class ChangeProductPrice (
    override val id: AggregateIdType,
    var price: Int,
    val originalVersion: Int
): Command()

abstract class AggregateRoot(
    val id: AggregateIdType,
    val events: List<Event>
) {
    private var changes: MutableList<Event> = mutableListOf()
    fun applyChange(event: Event) {
        changes.add(event)
    }

    fun getUncommittedChanges(): List<Event> {
        return changes
    }
}

class Product (
    id: AggregateIdType,
    events: List<Event> = emptyList()
): AggregateRoot(id, events) {
    constructor(
        id: AggregateIdType,
        name: String,
        price: Int
    ): this(id) {
        val createdProduct = ProductCreated(id, name, price)
        applyChange(createdProduct)
    }

    fun changePrice(newPrice: Int) {
//        var latestEvent = events.filter { it: Event -> it.}
//        if (latestEvent != null && latestEvent?.price == newPrice) throw ArgumentException("New price is already existed")
        if (newPrice < 0) throw ArgumentException("Price must not be negative")
        applyChange(ProductPriceChanged(id, newPrice))
    }
}

class ArgumentException(message: String) : Exception(message)

interface IRepository<A: AggregateRoot> {
    fun save(aggregate: A, expectedVersions: Int)
    fun getById(aggregateId: AggregateIdType): A
}

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
        return store[aggregateId]?.map { it.data } ?: emptyList()
    }

    fun saveEvents(aggregateId: AggregateIdType, events: List<Event>, expectedVersion: Int) {
       println("Save Events with id $aggregateId")
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
) : Exception(message)
