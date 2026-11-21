package ru.ifmo.se.runner.domain.model

import java.util.UUID

@JvmInline
value class ContestId(
    val value: UUID,
) {
    companion object {
        fun generate(): ContestId = ContestId(UUID.randomUUID())
    }
}
