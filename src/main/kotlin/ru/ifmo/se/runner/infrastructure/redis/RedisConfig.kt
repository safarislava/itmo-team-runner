package ru.ifmo.se.runner.infrastructure.redis

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import java.time.Duration

@Configuration
@EnableConfigurationProperties(SubmissionQueueProperties::class)
class RedisConfig {
    @Bean
    fun submissionStreamListenerContainer(
        connectionFactory: RedisConnectionFactory,
        properties: SubmissionQueueProperties,
    ): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        val options =
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(properties.pollTimeoutMs))
                .batchSize(1)
                .serializer(RedisSerializer.string())
                .autoStartup(false)
                .build()

        return StreamMessageListenerContainer.create(connectionFactory, options)
    }
}
