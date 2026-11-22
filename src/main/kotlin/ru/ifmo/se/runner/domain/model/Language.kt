package ru.ifmo.se.runner.domain.model

data class Language(
    val id: LanguageId,
    val name: String,
    val version: String,
    val sourceFilename: String,
    val compileCommand: String?,
    val runCommand: String,
)
