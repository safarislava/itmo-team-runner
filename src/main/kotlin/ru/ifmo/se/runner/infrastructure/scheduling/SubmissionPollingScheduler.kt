package ru.ifmo.se.runner.infrastructure.scheduling

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.ifmo.se.runner.application.service.SubmissionCheckingService

@Component
class SubmissionPollingScheduler(
    private val submissionCheckingService: SubmissionCheckingService,
) {
    @Scheduled(fixedDelayString = "\${runner.polling.interval-ms}")
    fun poll() {
        do {
            val submission = submissionCheckingService.processNextSubmission()
        } while (submission != null)
    }
}
