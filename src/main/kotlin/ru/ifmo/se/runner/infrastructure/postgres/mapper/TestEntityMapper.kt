package ru.ifmo.se.runner.infrastructure.postgres.mapper

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import ru.ifmo.se.runner.domain.model.TaskId
import ru.ifmo.se.runner.domain.model.Test
import ru.ifmo.se.runner.domain.model.TestId
import ru.ifmo.se.runner.infrastructure.postgres.entity.TaskEntity
import ru.ifmo.se.runner.infrastructure.postgres.entity.TestEntity

@Component
class TestEntityMapper(
    private val entityManager: EntityManager,
) {
    fun toDomain(entity: TestEntity): Test =
        Test(
            id = TestId(entity.id),
            taskId = TaskId(entity.task.id),
            inputS3key = entity.inputS3key,
            outputS3key = entity.outputS3key,
            isHidden = entity.isHidden,
        )

    fun toEntity(domain: Test): TestEntity =
        TestEntity(
            id = domain.id.value,
            task = entityManager.getReference(TaskEntity::class.java, domain.taskId.value),
            inputS3key = domain.inputS3key,
            outputS3key = domain.outputS3key,
            isHidden = domain.isHidden,
        )
}
