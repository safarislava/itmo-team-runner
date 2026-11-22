package ru.ifmo.se.runner.infrastructure.executor.isolate

data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
)
