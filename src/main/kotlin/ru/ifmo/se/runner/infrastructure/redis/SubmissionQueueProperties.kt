package ru.ifmo.se.runner.infrastructure.redis

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "runner.submission-queue")
data class SubmissionQueueProperties(
    val consumerGroup: String,
    val pollTimeoutMs: Long,
)
