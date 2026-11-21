package ru.ifmo.se.runner.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.runner.domain.model.RunnerServer
import ru.ifmo.se.runner.domain.model.RunnerServerId
import ru.ifmo.se.runner.infrastructure.postgres.entity.RunnerServerEntity

@Component
class RunnerServerEntityMapper {
    fun toDomain(entity: RunnerServerEntity): RunnerServer =
        RunnerServer(
            id = RunnerServerId(entity.id),
            ip = entity.ip,
        )

    fun toEntity(domain: RunnerServer): RunnerServerEntity =
        RunnerServerEntity(
            id = domain.id.value,
            ip = domain.ip,
        )
}
