package ru.ifmo.se.runner.infrastructure.server

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.runner.application.port.out.CurrentRunnerServerProvider
import ru.ifmo.se.runner.domain.model.RunnerServer
import ru.ifmo.se.runner.domain.model.RunnerServerId
import ru.ifmo.se.runner.domain.repository.RunnerServerRepository
import java.net.InetAddress
import java.net.NetworkInterface

@Component
class CurrentRunnerServerProviderImpl(
    private val runnerServerRepository: RunnerServerRepository,
) : CurrentRunnerServerProvider {
    @Volatile
    private var currentServerId: RunnerServerId? = null

    @PostConstruct
    @Transactional
    fun register() {
        val ip = resolveLocalIp()
        val server =
            runnerServerRepository.findByIp(ip) ?: run {
                val newServer =
                    RunnerServer(
                        id = RunnerServerId.generate(),
                        ip = ip,
                    )
                runnerServerRepository.save(newServer)
            }
        currentServerId = server.id
    }

    override fun getCurrentServerId(): RunnerServerId =
        currentServerId ?: throw IllegalStateException("Current runner server is not initialized yet.")

    private fun resolveLocalIp(): String =
        try {
            NetworkInterface
                .getNetworkInterfaces()
                .asSequence()
                .filter { it.isUp && !it.isLoopback }
                .flatMap { it.inetAddresses.asSequence() }
                .filter { !it.isLoopbackAddress && it.isSiteLocalAddress }
                .map { it.hostAddress }
                .firstOrNull() ?: InetAddress.getLocalHost().hostAddress
        } catch (ex: Exception) {
            error(ex)
        }
}
