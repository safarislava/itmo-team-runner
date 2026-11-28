package ru.ifmo.se.runner.infrastructure.executor.isolate

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

abstract class AbstractIsolateCommandRunner(
    private val commandTimeoutMs: Long,
) : IsolateCommandRunner {
    protected fun runCommand(
        command: List<String>,
        stdin: ByteArray?,
    ): CommandResult {
        val process = ProcessBuilder(command).start()
        val stdout = CompletableFuture.supplyAsync { process.inputStream.bufferedReader().use { it.readText() } }
        val stderr = CompletableFuture.supplyAsync { process.errorStream.bufferedReader().use { it.readText() } }

        stdin?.let { process.outputStream.use { os -> os.write(it) } }

        return if (process.awaitCompletion()) {
            CommandResult(process.exitValue(), stdout.get(), stderr.get())
        } else {
            CommandResult(-1, "", "Command timed out after ${commandTimeoutMs}ms")
        }
    }

    protected fun Process.awaitCompletion(): Boolean {
        if (waitFor(commandTimeoutMs, TimeUnit.MILLISECONDS)) {
            return true
        }
        destroyForcibly()
        return false
    }
}
