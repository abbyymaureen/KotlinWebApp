package com.abrown.KotlinCalculator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinCalculatorApplication

fun main(args: Array<String>) {
	runApplication<KotlinCalculatorApplication>(*args)
}
