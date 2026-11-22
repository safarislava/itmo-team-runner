package ru.ifmo.se.runner.application.dto

import ru.ifmo.se.runner.domain.model.Language
import ru.ifmo.se.runner.domain.model.SubmissionId

data class TestExecutionContext(
    val submissionId: SubmissionId,
    val language: Language,
    val timeLimit: Int,
    val memoryLimit: Int,
    val solutionSource: ByteArray,
    val tests: List<TestCaseData>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestExecutionContext

        if (submissionId != other.submissionId) return false
        if (language != other.language) return false
        if (timeLimit != other.timeLimit) return false
        if (memoryLimit != other.memoryLimit) return false
        if (!solutionSource.contentEquals(other.solutionSource)) return false
        if (tests != other.tests) return false

        return true
    }

    override fun hashCode(): Int {
        var result = submissionId.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + timeLimit
        result = 31 * result + memoryLimit
        result = 31 * result + solutionSource.contentHashCode()
        result = 31 * result + tests.hashCode()
        return result
    }
}
