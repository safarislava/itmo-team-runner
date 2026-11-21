package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.domain.model.SubmissionResponse

interface SubmissionResponseRepository {
    fun findById(submissionId: SubmissionId): SubmissionResponse?

    fun save(response: SubmissionResponse): SubmissionResponse

    fun delete(submissionId: SubmissionId)
}
