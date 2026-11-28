package ru.ifmo.se.runner.infrastructure.postgres

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SubmissionLeaseProperties::class)
class PostgresConfig
