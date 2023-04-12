package com.ericrochester.todo

import jakarta.persistence.*

@Entity
data class Todo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val title: String,
    val completed: Boolean = false,
    val taskOrder: Int = -1
)
