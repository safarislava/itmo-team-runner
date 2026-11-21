package ru.ifmo.se.runner.infrastructure.postgres.mapper

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import ru.ifmo.se.runner.domain.model.RunnerServerId
import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.domain.model.SubmissionResponse
import ru.ifmo.se.runner.infrastructure.postgres.entity.RunnerServerEntity
import ru.ifmo.se.runner.infrastructure.postgres.entity.SubmissionResponseEntity

@Component
class SubmissionResponseEntityMapper(
    private val entityManager: EntityManager,
) {
    fun toDomain(entity: SubmissionResponseEntity): SubmissionResponse =
        SubmissionResponse(
            submissionId = SubmissionId(entity.id),
            verdict = entity.verdict,
            executionTime = entity.executionTime,
            memoryUsed = entity.memoryUsed,
            runnerServerId = entity.runnerServer?.let { server -> RunnerServerId(server.id) },
        )

    fun toEntity(domain: SubmissionResponse): SubmissionResponseEntity =
        SubmissionResponseEntity(
            id = domain.submissionId.value,
            verdict = domain.verdict,
            executionTime = domain.executionTime,
            memoryUsed = domain.memoryUsed,
            runnerServer =
                domain.runnerServerId?.let { serverId ->
                    entityManager.getReference(RunnerServerEntity::class.java, serverId.value)
                },
        )
}
