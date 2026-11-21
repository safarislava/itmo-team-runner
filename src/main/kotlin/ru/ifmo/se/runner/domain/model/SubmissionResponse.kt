package ru.ifmo.se.runner.domain.model

data class SubmissionResponse(
    val submissionId: SubmissionId,
    val verdict: SubmissionVerdict?,
    val executionTime: Int?,
    val memoryUsed: Int?,
    val runnerServerId: RunnerServerId?,
)
