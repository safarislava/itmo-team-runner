package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.Task
import ru.ifmo.se.runner.domain.model.TaskId

interface TaskRepository {
    fun findById(id: TaskId): Task?

    fun findAll(): List<Task>
}
