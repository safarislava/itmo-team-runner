package ru.ifmo.se.runner.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.runner.infrastructure.postgres.entity.TaskEntity
import java.util.UUID

interface JpaTaskRepository : JpaRepository<TaskEntity, UUID>
