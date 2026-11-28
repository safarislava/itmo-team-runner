package ru.ifmo.se.runner.infrastructure.postgres.repository

import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.hibernate.cfg.AvailableSettings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param
import ru.ifmo.se.runner.domain.model.SubmissionStatus
import ru.ifmo.se.runner.infrastructure.postgres.entity.SubmissionEntity
import java.time.Instant
import java.util.UUID

interface JpaSubmissionRepository : JpaRepository<SubmissionEntity, UUID> {
    @Query(
        """
        SELECT s
        FROM SubmissionEntity s
        WHERE s.id = :id
          AND (
              s.status = ru.ifmo.se.runner.domain.model.SubmissionStatus.QUEUED
              OR (s.status = ru.ifmo.se.runner.domain.model.SubmissionStatus.RUNNING AND s.leaseUntil < :now)
          )
        """,
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = AvailableSettings.JAKARTA_LOCK_TIMEOUT, value = "-2"))
    fun findClaimable(
        @Param("id") id: UUID,
        @Param("now") now: Instant,
    ): SubmissionEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByIdAndStatusAndLeaseToken(
        id: UUID,
        status: SubmissionStatus,
        leaseToken: UUID,
    ): SubmissionEntity?

    @Modifying
    @Query(
        """
        UPDATE SubmissionEntity s
        SET s.leaseUntil = :leaseUntil
        WHERE s.id = :id
          AND s.status = ru.ifmo.se.runner.domain.model.SubmissionStatus.RUNNING
          AND s.leaseToken = :leaseToken
        """,
    )
    fun renewLease(
        @Param("id") id: UUID,
        @Param("leaseToken") leaseToken: UUID,
        @Param("leaseUntil") leaseUntil: Instant,
    ): Int
}
