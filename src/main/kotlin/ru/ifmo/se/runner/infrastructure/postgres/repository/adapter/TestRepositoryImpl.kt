package ru.ifmo.se.runner.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.runner.domain.model.TaskId
import ru.ifmo.se.runner.domain.model.Test
import ru.ifmo.se.runner.domain.repository.TestRepository
import ru.ifmo.se.runner.infrastructure.postgres.mapper.TestEntityMapper
import ru.ifmo.se.runner.infrastructure.postgres.repository.JpaTestRepository

@Repository
class TestRepositoryImpl(
    private val repo: JpaTestRepository,
    private val mapper: TestEntityMapper,
) : TestRepository {
    override fun findByTaskId(taskId: TaskId): List<Test> = repo.findByTaskId(taskId.value).map(mapper::toDomain)
}
