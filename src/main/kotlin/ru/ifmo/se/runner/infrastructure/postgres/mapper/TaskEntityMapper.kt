package ru.ifmo.se.runner.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.runner.domain.model.PersonId
import ru.ifmo.se.runner.domain.model.Task
import ru.ifmo.se.runner.domain.model.TaskId
import ru.ifmo.se.runner.infrastructure.postgres.entity.TaskEntity

@Component
class TaskEntityMapper {
    fun toDomain(entity: TaskEntity): Task =
        Task(
            id = TaskId(entity.id),
            authorId = PersonId(entity.authorId),
            name = entity.name,
            description = entity.description,
            timeLimit = entity.timeLimit,
            memoryLimit = entity.memoryLimit,
        )

    fun toEntity(domain: Task): TaskEntity =
        TaskEntity(
            id = domain.id.value,
            authorId = domain.authorId.value,
            name = domain.name,
            description = domain.description,
            timeLimit = domain.timeLimit,
            memoryLimit = domain.memoryLimit,
        )
}
