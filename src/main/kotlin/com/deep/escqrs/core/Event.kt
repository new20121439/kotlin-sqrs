package com.deep.escqrs.core

import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

abstract class Event : Message()

@Entity
@Table(
    name = "event",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["aggregate-id", "version"])
    ]
)
@TypeDef(name = "json", typeClass = JsonType::class)
data class EventDescriptor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private val id: Long?,

    @Column(name = "aggregate-id", nullable = false)
    val aggregateId: AggregateIdType,

    @Type(type = "json")
    @Column(nullable = false, columnDefinition = "json")
    val data: Event,

    @Column(nullable = false)
    val version: Int
)
