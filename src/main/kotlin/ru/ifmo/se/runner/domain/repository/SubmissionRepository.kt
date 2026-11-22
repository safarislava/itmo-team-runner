package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionId

interface SubmissionRepository {
    fun findById(id: SubmissionId): Submission?

    fun findAll(): List<Submission>

    fun save(submission: Submission): Submission

    fun findNextQueued(): Submission?
}
