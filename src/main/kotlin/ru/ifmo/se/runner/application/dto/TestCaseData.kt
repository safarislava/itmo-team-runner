package ru.ifmo.se.runner.application.dto

import ru.ifmo.se.runner.domain.model.TestId

data class TestCaseData(
    val testId: TestId,
    val input: ByteArray,
    val expectedOutput: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestCaseData

        if (testId != other.testId) return false
        if (!input.contentEquals(other.input)) return false
        if (!expectedOutput.contentEquals(other.expectedOutput)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = testId.hashCode()
        result = 31 * result + input.contentHashCode()
        result = 31 * result + expectedOutput.contentHashCode()
        return result
    }
}
