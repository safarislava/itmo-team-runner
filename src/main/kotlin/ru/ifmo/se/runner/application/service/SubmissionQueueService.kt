package ru.ifmo.se.runner.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionStatus
import ru.ifmo.se.runner.domain.repository.SubmissionRepository

@Service
class SubmissionQueueService(
    private val submissionRepository: SubmissionRepository,
) {
    @Transactional
    fun claimNextSubmission(): Submission? =
        submissionRepository.findNextQueued()?.let { submission ->
            submissionRepository.save(submission.copy(status = SubmissionStatus.RUNNING))
        }
}
