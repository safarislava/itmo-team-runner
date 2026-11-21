package ru.ifmo.se.runner.domain.model

data class Test(
    val id: TestId,
    val taskId: TaskId,
    val inputS3key: String,
    val outputS3key: String,
    val isHidden: Boolean,
)
