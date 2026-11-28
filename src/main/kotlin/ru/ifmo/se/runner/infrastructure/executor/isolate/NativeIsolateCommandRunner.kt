package ru.ifmo.se.runner.infrastructure.executor.isolate

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File

@Component
@ConditionalOnProperty(name = ["runner.isolate.mode"], havingValue = "native", matchIfMissing = true)
class NativeIsolateCommandRunner(
    @Value("\${runner.isolate.command-timeout-ms}")
    commandTimeoutMs: Long,
) : AbstractIsolateCommandRunner(commandTimeoutMs) {
    override fun execute(
        args: List<String>,
        stdin: ByteArray?,
    ): CommandResult = runCommand(listOf("isolate") + args, stdin)

    override fun writeFileToBox(
        boxPath: String,
        filename: String,
        content: ByteArray,
    ) = File(boxPath, filename).writeBytes(content)

    override fun readFileFromBox(
        boxPath: String,
        filename: String,
    ): ByteArray? = File(boxPath, filename).takeIf { it.exists() }?.readBytes()
}
