package ru.ifmo.se.runner.infrastructure.redis.consumer

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.stream.StreamListener
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.stereotype.Component
import ru.ifmo.se.runner.application.port.out.CurrentRunnerServerProvider
import ru.ifmo.se.runner.application.service.SubmissionCheckingService
import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.infrastructure.redis.SubmissionQueueProperties
import ru.ifmo.se.runner.infrastructure.redis.resolver.SubmissionQueueStreamKeyResolver
import java.util.UUID

@Component
class RedisSubmissionStreamConsumer(
    private val properties: SubmissionQueueProperties,
    streamKeyResolver: SubmissionQueueStreamKeyResolver,
    private val listenerContainer: StreamMessageListenerContainer<String, MapRecord<String, String, String>>,
    private val currentRunnerServerProvider: CurrentRunnerServerProvider,
    private val submissionCheckingService: SubmissionCheckingService,
) : StreamListener<String, MapRecord<String, String, String>> {
    private val streamKey = streamKeyResolver.resolveKey()
    private val consumerName: String by lazy {
        "runner-${currentRunnerServerProvider.getCurrentServerId().value}-${UUID.randomUUID()}"
    }

    @EventListener(ApplicationReadyEvent::class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun start() {
        val readRequest =
            StreamMessageListenerContainer.StreamReadRequest
                .builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                .consumer(Consumer.from(properties.consumerGroup, consumerName))
                .autoAcknowledge(true)
                .cancelOnError { false }
                .build()

        listenerContainer.register(readRequest, this)
        listenerContainer.start()
    }

    override fun onMessage(message: MapRecord<String, String, String>) {
        message.value[FIELD_SUBMISSION_ID]
            ?.let(SubmissionId::fromOrNull)
            ?.let(submissionCheckingService::processSubmission)
    }

    private companion object {
        private const val FIELD_SUBMISSION_ID = "submissionId"
    }
}
