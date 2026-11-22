package ru.ifmo.se.runner.infrastructure.postgres.repository

import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.hibernate.cfg.AvailableSettings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import ru.ifmo.se.runner.domain.model.SubmissionStatus
import ru.ifmo.se.runner.infrastructure.postgres.entity.SubmissionEntity
import java.util.UUID

interface JpaSubmissionRepository : JpaRepository<SubmissionEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = AvailableSettings.JAKARTA_LOCK_TIMEOUT, value = "-2"))
    fun findFirstByStatusOrderBySubmittedAtAsc(status: SubmissionStatus): SubmissionEntity?
}
