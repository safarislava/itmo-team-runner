package ru.ifmo.se.runner.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.domain.model.SubmissionStatus
import ru.ifmo.se.runner.domain.repository.SubmissionRepository
import ru.ifmo.se.runner.infrastructure.postgres.mapper.SubmissionEntityMapper
import ru.ifmo.se.runner.infrastructure.postgres.repository.JpaSubmissionRepository

@Repository
class SubmissionRepositoryImpl(
    private val repo: JpaSubmissionRepository,
    private val mapper: SubmissionEntityMapper,
) : SubmissionRepository {
    override fun findById(id: SubmissionId): Submission? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(): List<Submission> = repo.findAll().map(mapper::toDomain)

    override fun save(submission: Submission): Submission = mapper.toDomain(repo.save(mapper.toEntity(submission)))

    @Transactional
    override fun findQueuedById(id: SubmissionId): Submission? =
        repo.findByIdAndStatus(id.value, SubmissionStatus.QUEUED)?.let(mapper::toDomain)
}
