package ru.ifmo.se.runner.domain.model

import java.util.UUID

@JvmInline
value class TestId(
    val value: UUID,
) {
    companion object {
        fun generate(): TestId = TestId(UUID.randomUUID())
    }
}
