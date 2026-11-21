package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.TaskId
import ru.ifmo.se.runner.domain.model.Test
import ru.ifmo.se.runner.domain.model.TestId

interface TestRepository {
    fun findById(id: TestId): Test?
}
