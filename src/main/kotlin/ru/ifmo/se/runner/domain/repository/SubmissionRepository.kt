package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionId
import java.time.Instant
import java.util.UUID

interface SubmissionRepository {
    fun findById(id: SubmissionId): Submission?

    fun findAll(): List<Submission>

    fun save(submission: Submission): Submission

    fun findClaimable(
        id: SubmissionId,
        now: Instant,
    ): Submission?

    fun findOwnedRunning(
        id: SubmissionId,
        leaseToken: UUID,
    ): Submission?

    fun renewLease(
        id: SubmissionId,
        leaseToken: UUID,
        leaseUntil: Instant,
    ): Boolean
}
