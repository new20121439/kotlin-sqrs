import com.deep.escqrs.core.Event
import java.util.*

typealias AggregateIdType = UUID

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

interface IRepository<A: AggregateRoot> {
    fun save(aggregate: A, expectedVersions: Int)
    fun getById(aggregateId: AggregateIdType): A
}