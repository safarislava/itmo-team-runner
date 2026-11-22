package ru.ifmo.se.runner.infrastructure.executor.isolate

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File

@Component
@ConditionalOnProperty(name = ["runner.isolate.mode"], havingValue = "native", matchIfMissing = true)
class NativeIsolateCommandRunner : IsolateCommandRunner {
    override fun execute(
        args: List<String>,
        stdin: ByteArray?,
    ): CommandResult {
        val processBuilder = ProcessBuilder(listOf("isolate") + args)
        val process = processBuilder.start()

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
        val targetFile = File(boxPath, filename)
        targetFile.writeBytes(content)
    }

    override fun readFileFromBox(
        boxPath: String,
        filename: String,
    ): ByteArray? {
        val targetFile = File(boxPath, filename)
        return if (targetFile.exists()) targetFile.readBytes() else null
    }
}
