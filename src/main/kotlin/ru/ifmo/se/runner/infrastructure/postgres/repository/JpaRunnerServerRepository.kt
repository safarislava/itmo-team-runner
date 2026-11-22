package ru.ifmo.se.runner.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.runner.infrastructure.postgres.entity.RunnerServerEntity
import java.util.UUID

interface JpaRunnerServerRepository : JpaRepository<RunnerServerEntity, UUID> {
    fun findByIp(ip: String): RunnerServerEntity?
}
