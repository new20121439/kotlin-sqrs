package com.deep.escqrs.core

import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.util.*
import javax.persistence.*

abstract class Event : Message()

@Entity
@Table(
    name = "events",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["aggregate_id", "version"])
    ]
)
@TypeDef(name = "json", typeClass = JsonType::class)
data class EventDescriptor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private val id: Long?,

    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: UUID,

    @Type(type = "json")
    @Column(nullable = false, columnDefinition = "json")
    val data: Event,

    @Column(nullable = false)
    val version: Int
)

interface EventHandler<E: Event>{
    fun handle(event: E)
}
