package ru.ifmo.se.runner.domain.model

data class SubmissionResponse(
    val verdict: SubmissionVerdict?,
    val executionTime: Int?,
    val memoryUsed: Int?,
    val runnerServerId: RunnerServerId?,
)
