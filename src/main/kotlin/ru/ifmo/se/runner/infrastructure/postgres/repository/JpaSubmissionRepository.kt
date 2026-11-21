package ru.ifmo.se.runner.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.runner.infrastructure.postgres.entity.SubmissionEntity
import java.util.UUID

interface JpaSubmissionRepository : JpaRepository<SubmissionEntity, UUID>
