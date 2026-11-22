package ru.ifmo.se.runner.application.port.out

import ru.ifmo.se.runner.application.dto.TestExecutionContext
import ru.ifmo.se.runner.application.dto.TestExecutionResult

interface TestExecutor {
    fun execute(context: TestExecutionContext): TestExecutionResult
}
