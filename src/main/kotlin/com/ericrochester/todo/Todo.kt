package com.ericrochester.todo

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Todo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val title: String,
    val completed: Boolean = false,
    val order: Int = -1
)
