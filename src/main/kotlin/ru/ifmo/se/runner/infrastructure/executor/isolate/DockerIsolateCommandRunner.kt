package ru.ifmo.se.runner.infrastructure.executor.isolate

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
@ConditionalOnProperty(name = ["runner.isolate.mode"], havingValue = "docker")
class DockerIsolateCommandRunner(
    @Value("\${runner.isolate.docker.container}")
    private val containerName: String,
    @Value("\${runner.isolate.command-timeout-ms}")
    commandTimeoutMs: Long,
) : AbstractIsolateCommandRunner(commandTimeoutMs) {
    override fun execute(
        args: List<String>,
        stdin: ByteArray?,
    ): CommandResult = runCommand(listOf("docker", "exec", "-i", containerName, "isolate") + args, stdin)

    override fun writeFileToBox(
        boxPath: String,
        filename: String,
        content: ByteArray,
    ) {
        val process = ProcessBuilder("docker", "exec", "-i", containerName, "sh", "-c", "cat > '$boxPath/$filename'").start()
        process.outputStream.use { it.write(content) }
        check(process.awaitCompletion()) { "Timed out writing file to isolate box" }
    }

    override fun readFileFromBox(
        boxPath: String,
        filename: String,
    ): ByteArray? {
        val process = ProcessBuilder("docker", "exec", "-i", containerName, "cat", "$boxPath/$filename").start()
        val bytes = CompletableFuture.supplyAsync { process.inputStream.use { it.readAllBytes() } }
        return if (process.awaitCompletion() && process.exitValue() == 0) bytes.get() else null
    }
}
