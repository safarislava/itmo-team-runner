package ru.ifmo.se.runner.application.port.out

import ru.ifmo.se.runner.domain.model.RunnerServerId

interface CurrentRunnerServerProvider {
    fun getCurrentServerId(): RunnerServerId
}
