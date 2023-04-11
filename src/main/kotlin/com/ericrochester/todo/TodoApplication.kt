package com.ericrochester.todo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@SpringBootApplication
@ServletComponentScan
class TodoApplication

fun main(args: Array<String>) {
	runApplication<TodoApplication>(*args)
}
