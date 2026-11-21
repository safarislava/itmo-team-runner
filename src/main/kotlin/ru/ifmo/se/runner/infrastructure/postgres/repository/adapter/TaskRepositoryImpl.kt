package ru.ifmo.se.runner.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.runner.domain.model.Task
import ru.ifmo.se.runner.domain.model.TaskId
import ru.ifmo.se.runner.domain.repository.TaskRepository
import ru.ifmo.se.runner.infrastructure.postgres.mapper.TaskEntityMapper
import ru.ifmo.se.runner.infrastructure.postgres.repository.JpaTaskRepository

@Repository
class TaskRepositoryImpl(
    private val repo: JpaTaskRepository,
    private val mapper: TaskEntityMapper,
) : TaskRepository {
    override fun findById(id: TaskId): Task? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(): List<Task> = repo.findAll().map(mapper::toDomain)
}
