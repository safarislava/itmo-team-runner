package ru.ifmo.se.runner.application.port.out

import java.time.Duration

interface SubmissionLeaseSettings {
    val ttl: Duration
    val renewInterval: Duration
    val maxDuration: Duration
    val maxRetries: Int
}
