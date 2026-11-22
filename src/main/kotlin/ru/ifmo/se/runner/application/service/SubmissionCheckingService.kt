package ru.ifmo.se.runner.application.service

import org.springframework.stereotype.Service
import ru.ifmo.se.runner.application.dto.TestCaseData
import ru.ifmo.se.runner.application.dto.TestExecutionContext
import ru.ifmo.se.runner.application.dto.TestExecutionResult
import ru.ifmo.se.runner.application.port.out.CurrentRunnerServerProvider
import ru.ifmo.se.runner.application.port.out.FileStorage
import ru.ifmo.se.runner.application.port.out.TestExecutor
import ru.ifmo.se.runner.domain.exception.LanguageNotFoundException
import ru.ifmo.se.runner.domain.exception.TaskNotFoundException
import ru.ifmo.se.runner.domain.model.RunnerServerId
import ru.ifmo.se.runner.domain.model.Submission
import ru.ifmo.se.runner.domain.model.SubmissionResponse
import ru.ifmo.se.runner.domain.model.SubmissionStatus
import ru.ifmo.se.runner.domain.model.SubmissionVerdict
import ru.ifmo.se.runner.domain.repository.LanguageRepository
import ru.ifmo.se.runner.domain.repository.SubmissionRepository
import ru.ifmo.se.runner.domain.repository.TaskRepository
import ru.ifmo.se.runner.domain.repository.TestRepository

@Service
class SubmissionCheckingService(
    private val queueService: SubmissionQueueService,
    private val currentRunnerServerProvider: CurrentRunnerServerProvider,
    private val languageRepository: LanguageRepository,
    private val taskRepository: TaskRepository,
    private val testRepository: TestRepository,
    private val fileStorage: FileStorage,
    private val testExecutor: TestExecutor,
    private val submissionRepository: SubmissionRepository,
) {
    fun processNextSubmission(): Submission? {
        val submission = queueService.claimNextSubmission() ?: return null

        val result =
            runCatching { testExecutor.execute(buildContext(submission)) }
                .getOrElse { TestExecutionResult(verdict = SubmissionVerdict.RUNTIME_ERROR) }

        return saveResult(submission, result)
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

    private fun saveResult(
        submission: Submission,
        result: TestExecutionResult,
    ): Submission {
        val isSuccess = result.verdict == SubmissionVerdict.OK
        val response =
            SubmissionResponse(
                submissionId = submission.id,
                verdict = result.verdict,
                executionTime = result.executionTime,
                memoryUsed = result.memoryUsed,
                runnerServerId = currentRunnerServerProvider.getCurrentServerId(),
            )

        return submissionRepository.save(
            submission.copy(
                status = if (isSuccess) SubmissionStatus.FINISHED else SubmissionStatus.FAILED,
                response = response,
            ),
        )
    }
}
