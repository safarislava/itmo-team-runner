package ru.ifmo.se.runner.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.runner.infrastructure.postgres.entity.SubmissionResponseEntity
import java.util.UUID

interface JpaSubmissionResponseRepository : JpaRepository<SubmissionResponseEntity, UUID>
