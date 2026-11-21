package ru.ifmo.se.runner.domain.model

import java.util.UUID

@JvmInline
value class TaskId(
    val value: UUID,
) {
    companion object {
        fun generate(): TaskId = TaskId(UUID.randomUUID())
    }
}
