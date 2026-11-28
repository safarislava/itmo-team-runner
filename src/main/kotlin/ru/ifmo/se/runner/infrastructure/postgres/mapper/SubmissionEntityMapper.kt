package ru.ifmo.se.runner.infrastructure.postgres.mapper

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import ru.ifmo.se.runner.domain.model.ContestId
import ru.ifmo.se.runner.domain.model.LanguageId
import ru.ifmo.se.runner.domain.model.PersonId
import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.domain.model.TaskId
import ru.ifmo.se.runner.infrastructure.postgres.entity.LanguageEntity
import ru.ifmo.se.runner.infrastructure.postgres.entity.SubmissionEntity
import ru.ifmo.se.runner.infrastructure.postgres.entity.TaskEntity

@Component
class SubmissionEntityMapper(
    private val entityManager: EntityManager,
    private val responseMapper: SubmissionResponseEntityMapper,
) {
    fun toDomain(entity: SubmissionEntity): Submission =
        Submission(
            id = SubmissionId(entity.id),
            senderId = PersonId(entity.senderId),
            contestId = ContestId(entity.contestId),
            taskId = TaskId(entity.task.id),
            languageId = LanguageId(entity.language.id),
            snapshotS3key = entity.snapshotS3key,
            status = entity.status,
            attempt = entity.attempt.toInt(),
            aiScore = entity.aiScore?.toInt(),
            submittedAt = entity.submittedAt,
            leaseToken = entity.leaseToken,
            leaseUntil = entity.leaseUntil,
            retryCount = entity.retryCount.toInt(),
            response = entity.response?.let(responseMapper::toDomain),
        )

    fun toEntity(domain: Submission): SubmissionEntity =
        SubmissionEntity(
            id = domain.id.value,
            senderId = domain.senderId.value,
            contestId = domain.contestId.value,
            task = entityManager.getReference(TaskEntity::class.java, domain.taskId.value),
            language = entityManager.getReference(LanguageEntity::class.java, domain.languageId.value),
            snapshotS3key = domain.snapshotS3key,
            status = domain.status,
            attempt = domain.attempt.toShort(),
            aiScore = domain.aiScore?.toShort(),
            submittedAt = domain.submittedAt,
            leaseToken = domain.leaseToken,
            leaseUntil = domain.leaseUntil,
            retryCount = domain.retryCount.toShort(),
            response = domain.response?.let(responseMapper::toEntity),
        )
}
