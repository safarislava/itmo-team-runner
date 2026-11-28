package ru.ifmo.se.runner.infrastructure.postgres

import org.springframework.boot.context.properties.ConfigurationProperties
import ru.ifmo.se.runner.application.port.out.SubmissionLeaseSettings
import java.time.Duration

@ConfigurationProperties(prefix = "runner.lease")
data class SubmissionLeaseProperties(
    val ttlMs: Long,
    val renewIntervalMs: Long,
    val maxDurationMs: Long,
    override val maxRetries: Int,
) : SubmissionLeaseSettings {
    override val ttl: Duration
        get() = Duration.ofMillis(ttlMs)

    override val renewInterval: Duration
        get() = Duration.ofMillis(renewIntervalMs)

    override val maxDuration: Duration
        get() = Duration.ofMillis(maxDurationMs)
}
