package ru.ifmo.se.runner.infrastructure.scheduling

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.ifmo.se.runner.infrastructure.redis.consumer.RedisSubmissionStreamConsumer

@Component
@ConditionalOnProperty(
    name = ["runner.submission-queue.recovery.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class SubmissionStreamRecoveryScheduler(
    private val streamConsumer: RedisSubmissionStreamConsumer,
) {
    @Scheduled(
        fixedDelayString = "\${runner.submission-queue.recovery.delay-ms}",
    )
    fun recoverPendingSubmissions() {
        streamConsumer.recoverPending()
    }
}
