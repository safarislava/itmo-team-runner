package ru.ifmo.se.runner.application.dto

import ru.ifmo.se.runner.domain.model.SubmissionVerdict

data class TestExecutionResult(
    val verdict: SubmissionVerdict,
    val executionTime: Int? = null,
    val memoryUsed: Int? = null,
)
