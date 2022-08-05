package com.deep.escqrs.core

import java.util.*

abstract class Message{
    abstract val id: UUID
}

abstract class Command : Message()

abstract class Event : Message()
