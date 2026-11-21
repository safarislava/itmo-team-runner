package ru.ifmo.se.runner.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.runner.infrastructure.postgres.entity.TestEntity
import java.util.UUID

interface JpaTestRepository : JpaRepository<TestEntity, UUID>
