package com.ericrochester.todo

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/todo")
class TodoController(@Autowired val repository: TodoRepository, @Autowired val servletRequest: HttpServletRequest) {

    @GetMapping
    fun getAllTodos(): List<TodoDTO> {
        val todos = repository.findAll()
        return todos.map { TodoDTO(it, servletRequest) }
    }

    @PostMapping
    fun createTodo(@RequestBody todoRequest: TodoRequest): ResponseEntity<TodoDTO> {
        val title = todoRequest.title ?: return ResponseEntity.badRequest().build()
        val todo = Todo(
            title = title,
            completed = todoRequest.completed ?: false,
            taskOrder = todoRequest.taskOrder ?: -1
        )
        val createdTodo = repository.save(todo)
        val location = URI.create("/todo/${createdTodo.id}")
        return ResponseEntity.created(location).body(TodoDTO(createdTodo, servletRequest))
    }

    @DeleteMapping
    fun deleteAllTodos(): ResponseEntity<Void> {
        repository.deleteAll()
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long): ResponseEntity<TodoDTO> =
        repository.findById(id).map { ResponseEntity.ok(TodoDTO(it, servletRequest)) }.orElse(ResponseEntity.notFound().build())

    @PatchMapping("/{id}")
    fun updateTodoById(@PathVariable id: Long, @RequestBody todoRequest: TodoRequest): ResponseEntity<TodoDTO> {
        val updatedTodo = repository.findById(id).map {
            val newTodo = it.copy(
                title = todoRequest.title ?: it.title,
                completed = todoRequest.completed ?: it.completed,
                taskOrder = todoRequest.taskOrder ?: it.taskOrder
            )
            repository.save(newTodo)
        }.orElse(null)

        return if (updatedTodo != null) ResponseEntity.ok(TodoDTO(updatedTodo, servletRequest)) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteTodoById(@PathVariable id: Long): ResponseEntity<Void> {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class TodoDTO(
    val id: Long?,
    val title: String,
    val completed: Boolean = false,
    @JsonProperty("order")
    val taskOrder: Int = -1,
    val url: String
) {
    constructor(todo: Todo, request: HttpServletRequest) : this(
        todo.id,
        todo.title,
        todo.completed,
        todo.taskOrder,
        "${request.scheme}://${request.serverName}:${request.serverPort}${request.contextPath}/todo/${todo.id}"
    )
}

data class TodoRequest(
    val title: String?,
    val completed: Boolean?,
    @JsonProperty("order") val taskOrder: Int?
)
