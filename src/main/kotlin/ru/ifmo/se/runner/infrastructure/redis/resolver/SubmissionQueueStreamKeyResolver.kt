package ru.ifmo.se.runner.infrastructure.redis.resolver

import org.springframework.stereotype.Component

@Component
class SubmissionQueueStreamKeyResolver {
    fun resolveKey(): String = STREAM_KEY

    private companion object {
        private const val STREAM_KEY = "submission:queue:stream"
    }
}
