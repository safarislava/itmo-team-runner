package ru.ifmo.se.runner.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.domain.model.SubmissionStatus
import ru.ifmo.se.runner.domain.repository.SubmissionRepository
import ru.ifmo.se.runner.infrastructure.postgres.mapper.SubmissionEntityMapper
import ru.ifmo.se.runner.infrastructure.postgres.repository.JpaSubmissionRepository
import java.time.Instant
import java.util.UUID

@Repository
class SubmissionRepositoryImpl(
    private val repo: JpaSubmissionRepository,
    private val mapper: SubmissionEntityMapper,
) : SubmissionRepository {
    override fun findById(id: SubmissionId): Submission? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(): List<Submission> = repo.findAll().map(mapper::toDomain)

    override fun save(submission: Submission): Submission = mapper.toDomain(repo.save(mapper.toEntity(submission)))

    override fun findClaimable(
        id: SubmissionId,
        now: Instant,
    ): Submission? = repo.findClaimable(id.value, now)?.let(mapper::toDomain)

    override fun findOwnedRunning(
        id: SubmissionId,
        leaseToken: UUID,
    ): Submission? = repo.findByIdAndStatusAndLeaseToken(id.value, SubmissionStatus.RUNNING, leaseToken)?.let(mapper::toDomain)

    override fun renewLease(
        id: SubmissionId,
        leaseToken: UUID,
        leaseUntil: Instant,
    ): Boolean = repo.renewLease(id.value, leaseToken, leaseUntil) > 0
}
