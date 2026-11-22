package ru.ifmo.se.runner.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.runner.domain.model.RunnerServer
import ru.ifmo.se.runner.domain.model.RunnerServerId
import ru.ifmo.se.runner.domain.repository.RunnerServerRepository
import ru.ifmo.se.runner.infrastructure.postgres.mapper.RunnerServerEntityMapper
import ru.ifmo.se.runner.infrastructure.postgres.repository.JpaRunnerServerRepository

@Repository
class RunnerServerRepositoryImpl(
    private val repo: JpaRunnerServerRepository,
    private val mapper: RunnerServerEntityMapper,
) : RunnerServerRepository {
    override fun findById(id: RunnerServerId): RunnerServer? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findByIp(ip: String): RunnerServer? = repo.findByIp(ip)?.let(mapper::toDomain)

    override fun findAll(): List<RunnerServer> = repo.findAll().map(mapper::toDomain)

    override fun save(runnerServer: RunnerServer): RunnerServer = mapper.toDomain(repo.save(mapper.toEntity(runnerServer)))
}
