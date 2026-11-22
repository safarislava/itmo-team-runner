package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.TaskId
import ru.ifmo.se.runner.domain.model.Test

interface TestRepository {
    fun findByTaskId(taskId: TaskId): List<Test>
}
