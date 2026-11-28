package ru.ifmo.se.runner.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.runner.application.dto.TestExecutionResult
import ru.ifmo.se.runner.domain.model.RunnerServerId
import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.domain.model.SubmissionResponse
import ru.ifmo.se.runner.domain.model.SubmissionStatus
import ru.ifmo.se.runner.domain.model.SubmissionVerdict
import ru.ifmo.se.runner.domain.repository.SubmissionRepository
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class SubmissionQueueService(
    private val submissionRepository: SubmissionRepository,
) {
    @Transactional
    fun claimSubmission(
        submissionId: SubmissionId,
        runnerServerId: RunnerServerId,
        leaseTtl: Duration,
        maxRetries: Int,
    ): Submission? {
        val now = Instant.now()
        val submission = submissionRepository.findClaimable(submissionId, now) ?: return null

        if (submission.retryCount >= maxRetries) {
            submissionRepository.save(
                submission.copy(
                    status = SubmissionStatus.FAILED,
                    leaseToken = null,
                    leaseUntil = null,
                    response = SubmissionResponse(submission.id, SubmissionVerdict.RUNTIME_ERROR, null, null, runnerServerId),
                ),
            )
            return null
        }

        return submissionRepository.save(
            submission.copy(
                status = SubmissionStatus.RUNNING,
                leaseToken = UUID.randomUUID(),
                leaseUntil = now.plus(leaseTtl),
                retryCount = submission.retryCount + 1,
            ),
        )
    }

    @Transactional
    fun renewLease(
        submissionId: SubmissionId,
        leaseToken: UUID,
        leaseTtl: Duration,
    ): Boolean = submissionRepository.renewLease(submissionId, leaseToken, Instant.now().plus(leaseTtl))

    @Transactional
    fun completeSubmission(
        submissionId: SubmissionId,
        leaseToken: UUID,
        result: TestExecutionResult,
        runnerServerId: RunnerServerId,
    ): Submission? {
        val submission = submissionRepository.findOwnedRunning(submissionId, leaseToken) ?: return null
        val status = if (result.verdict == SubmissionVerdict.OK) SubmissionStatus.FINISHED else SubmissionStatus.FAILED

        return submissionRepository.save(
            submission.copy(
                status = status,
                leaseToken = null,
                leaseUntil = null,
                response =
                    SubmissionResponse(
                        submissionId = submission.id,
                        verdict = result.verdict,
                        executionTime = result.executionTime,
                        memoryUsed = result.memoryUsed,
                        runnerServerId = runnerServerId,
                    ),
            ),
        )
    }
}
