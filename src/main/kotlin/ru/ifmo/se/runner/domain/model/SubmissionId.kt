package ru.ifmo.se.runner.domain.model

import java.util.UUID

@JvmInline
value class SubmissionId(
    val value: UUID,
) {
    companion object {
        fun generate(): SubmissionId = SubmissionId(UUID.randomUUID())
    }
}
