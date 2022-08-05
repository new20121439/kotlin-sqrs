package com.deep.escqrs.core

import java.util.*

abstract class Message{
    abstract val id: UUID
}
