package com.deep.escqrs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EsCqrsApplication

fun main(args: Array<String>) {
	runApplication<EsCqrsApplication>(*args)
}
