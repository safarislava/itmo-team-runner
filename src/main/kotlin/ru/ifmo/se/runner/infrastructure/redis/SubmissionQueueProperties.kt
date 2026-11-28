package ru.ifmo.se.runner.infrastructure.redis

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "runner.submission-queue")
data class SubmissionQueueProperties(
    val consumerGroup: String,
    val consumerName: String,
    val pollTimeoutMs: Long,
    val recovery: RecoveryProperties,
) {
    data class RecoveryProperties(
        val enabled: Boolean,
        val delayMs: Long,
        val minIdleMs: Long,
        val batchSize: Long,
    )
}
