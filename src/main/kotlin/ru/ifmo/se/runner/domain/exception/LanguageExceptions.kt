package ru.ifmo.se.runner.domain.exception

class LanguageNotFoundException(
    message: String = "Language not found.",
) : RuntimeException(message)

class LanguageBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
