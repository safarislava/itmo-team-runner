package ru.ifmo.se.runner.infrastructure.executor.isolate

import org.springframework.stereotype.Component
import ru.ifmo.se.runner.application.dto.TestExecutionContext
import ru.ifmo.se.runner.application.dto.TestExecutionResult
import ru.ifmo.se.runner.application.port.out.TestExecutor
import ru.ifmo.se.runner.domain.model.SubmissionVerdict
import java.util.concurrent.atomic.AtomicInteger

@Component
class IsolateTestExecutor(
    private val commandRunner: IsolateCommandRunner,
) : TestExecutor {
    private val boxIdCounter = AtomicInteger(0)

    override fun execute(context: TestExecutionContext): TestExecutionResult =
        withBox { boxId, boxPath ->
            val lang = context.language
            commandRunner.writeFileToBox(boxPath, lang.sourceFilename, context.solutionSource)

            if (!compile(boxId, boxPath, lang.compileCommand)) {
                return@withBox TestExecutionResult(SubmissionVerdict.COMPILATION_ERROR)
            }

            runTests(boxId, boxPath, context)
        } ?: TestExecutionResult(SubmissionVerdict.RUNTIME_ERROR)

    private fun compile(
        boxId: Int,
        boxPath: String,
        command: String?,
    ): Boolean {
        if (command == null) return true
        val args =
            listOf(
                "--box-id=$boxId",
                "--processes=10",
                "--wall-time=30",
                "--meta=$boxPath/compile_meta.txt",
                "--run",
                "--",
            ) + command.split("\\s+".toRegex())
        return commandRunner.execute(args).exitCode == 0
    }

    private fun runTests(
        boxId: Int,
        boxPath: String,
        context: TestExecutionContext,
    ): TestExecutionResult {
        val runCmd = context.language.runCommand.split("\\s+".toRegex())
        val timeLimitSec = (context.timeLimit.toDouble() / 1000.0).coerceAtLeast(0.1)
        val memoryLimitKb = context.memoryLimit * 1024
        var maxTimeMs = 0
        var maxMemoryKb = 0

        for ((index, test) in context.tests.withIndex()) {
            val metaFile = "meta_$index.txt"
            val args =
                listOf(
                    "--box-id=$boxId",
                    "--time=$timeLimitSec",
                    "--wall-time=${timeLimitSec * 2}",
                    "--mem=$memoryLimitKb",
                    "--meta=$boxPath/$metaFile",
                    "--run",
                    "--",
                ) + runCmd

            val result = commandRunner.execute(args, stdin = test.input)
            val meta =
                commandRunner
                    .readFileFromBox(boxPath, metaFile)
                    ?.toString(Charsets.UTF_8)
                    ?.let { IsolateMeta.parse(it) } ?: IsolateMeta()

            maxTimeMs = maxOf(maxTimeMs, (meta.time * 1000).toInt())
            maxMemoryKb = maxOf(maxMemoryKb, meta.memoryKb)

            val verdict = resolveVerdict(meta, result.stdout, test.expectedOutput)
            if (verdict != null) {
                return TestExecutionResult(verdict, maxTimeMs, maxMemoryKb)
            }
        }

        return TestExecutionResult(SubmissionVerdict.OK, maxTimeMs, maxMemoryKb)
    }

    private fun resolveVerdict(
        meta: IsolateMeta,
        stdout: String,
        expected: ByteArray,
    ): SubmissionVerdict? =
        when {
            meta.status == "TO" -> SubmissionVerdict.TIME_LIMIT
            meta.status == "MO" -> SubmissionVerdict.MEMORY_LIMIT
            meta.status in setOf("RE", "SG") || meta.exitCode != 0 -> SubmissionVerdict.RUNTIME_ERROR
            stdout.normalize() != expected.normalize() -> SubmissionVerdict.WRONG_ANSWER
            else -> null
        }

    private inline fun <T> withBox(block: (Int, String) -> T): T? {
        val boxId = (boxIdCounter.getAndIncrement() % 1000).coerceAtLeast(0)
        val init = commandRunner.execute(listOf("--box-id=$boxId", "--init"))
        if (init.exitCode != 0) return null
        return try {
            block(boxId, init.stdout.trim())
        } finally {
            commandRunner.execute(listOf("--box-id=$boxId", "--cleanup"))
        }
    }

    private fun String.normalize() = trim().replace("\r\n", "\n")

    private fun ByteArray.normalize() = toString(Charsets.UTF_8).normalize()
}
