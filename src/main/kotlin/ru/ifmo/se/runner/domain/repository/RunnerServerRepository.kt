package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.RunnerServer
import ru.ifmo.se.runner.domain.model.RunnerServerId

interface RunnerServerRepository {
    fun findById(id: RunnerServerId): RunnerServer?

    fun findAll(): List<RunnerServer>
}
