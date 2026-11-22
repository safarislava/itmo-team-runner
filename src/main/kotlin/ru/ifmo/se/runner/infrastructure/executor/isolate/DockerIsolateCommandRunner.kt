package ru.ifmo.se.runner.infrastructure.executor.isolate

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["runner.isolate.mode"], havingValue = "docker")
class DockerIsolateCommandRunner(
    @Value("\${runner.isolate.docker.container}")
    private val containerName: String,
) : IsolateCommandRunner {
    override fun execute(
        args: List<String>,
        stdin: ByteArray?,
    ): CommandResult {
        val command = listOf("docker", "exec", "-i", containerName, "isolate") + args
        val process = ProcessBuilder(command).start()

        stdin?.let {
            process.outputStream.use { os -> os.write(it) }
        }

        val stdout = process.inputStream.bufferedReader().use { it.readText() }
        val stderr = process.errorStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        return CommandResult(exitCode, stdout, stderr)
    }

    override fun writeFileToBox(
        boxPath: String,
        filename: String,
        content: ByteArray,
    ) {
        val destPath = "$boxPath/$filename"
        val process =
            ProcessBuilder(listOf("docker", "exec", "-i", containerName, "sh", "-c", "cat > '$destPath'"))
                .start()

        process.outputStream.use { os -> os.write(content) }
        process.waitFor()
    }

    override fun readFileFromBox(
        boxPath: String,
        filename: String,
    ): ByteArray? {
        val filePath = "$boxPath/$filename"
        val process =
            ProcessBuilder(listOf("docker", "exec", "-i", containerName, "cat", filePath))
                .start()

        val bytes = process.inputStream.readAllBytes()
        val exitCode = process.waitFor()
        return if (exitCode == 0) bytes else null
    }
}
