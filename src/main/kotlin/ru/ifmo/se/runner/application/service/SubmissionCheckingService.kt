package ru.ifmo.se.runner.application.service

import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import ru.ifmo.se.runner.application.dto.TestCaseData
import ru.ifmo.se.runner.application.dto.TestExecutionContext
import ru.ifmo.se.runner.application.dto.TestExecutionResult
import ru.ifmo.se.runner.application.port.out.CurrentRunnerServerProvider
import ru.ifmo.se.runner.application.port.out.FileStorage
import ru.ifmo.se.runner.application.port.out.SubmissionLeaseSettings
import ru.ifmo.se.runner.application.port.out.TestExecutor
import ru.ifmo.se.runner.domain.exception.LanguageNotFoundException
import ru.ifmo.se.runner.domain.exception.TaskNotFoundException
import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionId
import ru.ifmo.se.runner.domain.model.SubmissionVerdict
import ru.ifmo.se.runner.domain.repository.LanguageRepository
import ru.ifmo.se.runner.domain.repository.TaskRepository
import ru.ifmo.se.runner.domain.repository.TestRepository
import java.time.Instant
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Service
class SubmissionCheckingService(
    private val queueService: SubmissionQueueService,
    private val leaseSettings: SubmissionLeaseSettings,
    private val currentRunnerServerProvider: CurrentRunnerServerProvider,
    private val languageRepository: LanguageRepository,
    private val taskRepository: TaskRepository,
    private val testRepository: TestRepository,
    private val fileStorage: FileStorage,
    private val testExecutor: TestExecutor,
) {
    private val heartbeatExecutor =
        Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "submission-lease-heartbeat").apply { isDaemon = true }
        }

    @PreDestroy
    fun shutdown() {
        heartbeatExecutor.shutdownNow()
    }

    fun processSubmission(submissionId: SubmissionId): Submission? {
        val serverId = currentRunnerServerProvider.getCurrentServerId()
        val submission =
            queueService.claimSubmission(submissionId, serverId, leaseSettings.ttl, leaseSettings.maxRetries)
                ?: return null
        val leaseToken = submission.leaseToken ?: return null
        val heartbeat = scheduleHeartbeat(submissionId, leaseToken)
        return try {
            val result =
                runCatching { testExecutor.execute(buildContext(submission)) }
                    .getOrElse { TestExecutionResult(verdict = SubmissionVerdict.RUNTIME_ERROR) }
            queueService.completeSubmission(submission.id, leaseToken, result, serverId)
        } finally {
            heartbeat.cancel(false)
        }
    }

    private fun scheduleHeartbeat(
        submissionId: SubmissionId,
        leaseToken: UUID,
    ): ScheduledFuture<*> {
        val deadline = Instant.now().plus(leaseSettings.maxDuration)
        val intervalMs = leaseSettings.renewInterval.toMillis()
        var active = true

        return heartbeatExecutor.scheduleAtFixedRate(
            {
                if (active && Instant.now().isBefore(deadline)) {
                    active =
                        runCatching {
                            queueService.renewLease(submissionId, leaseToken, leaseSettings.ttl)
                        }.getOrDefault(false)
                }
            },
            intervalMs,
            intervalMs,
            TimeUnit.MILLISECONDS,
        )
    }

    private fun buildContext(submission: Submission): TestExecutionContext {
        val language =
            languageRepository.findById(submission.languageId)
                ?: throw LanguageNotFoundException("Language ${submission.languageId.value} not found")
        val task =
            taskRepository.findById(submission.taskId)
                ?: throw TaskNotFoundException("Task ${submission.taskId.value} not found")

        val tests =
            testRepository.findByTaskId(task.id).map { test ->
                TestCaseData(test.id, fileStorage.load(test.inputS3key), fileStorage.load(test.outputS3key))
            }

        return TestExecutionContext(
            submissionId = submission.id,
            language = language,
            timeLimit = task.timeLimit,
            memoryLimit = task.memoryLimit,
            solutionSource = fileStorage.load(submission.snapshotS3key),
            tests = tests,
        )
    }
}
