package ru.ifmo.se.runner.domain.model

import java.util.UUID

@JvmInline
value class SubmissionId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): SubmissionId = SubmissionId(UUID.randomUUID())

        fun fromOrNull(raw: String): SubmissionId? = runCatching { SubmissionId(UUID.fromString(raw)) }.getOrNull()
    }
}
