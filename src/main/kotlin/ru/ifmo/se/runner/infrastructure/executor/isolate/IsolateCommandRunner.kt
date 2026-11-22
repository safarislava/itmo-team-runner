package ru.ifmo.se.runner.infrastructure.executor.isolate

interface IsolateCommandRunner {
    fun execute(
        args: List<String>,
        stdin: ByteArray? = null,
    ): CommandResult

    fun writeFileToBox(
        boxPath: String,
        filename: String,
        content: ByteArray,
    )

    fun readFileFromBox(
        boxPath: String,
        filename: String,
    ): ByteArray?
}
