package ru.ifmo.se.runner.domain.repository

import ru.ifmo.se.runner.domain.model.Language
import ru.ifmo.se.runner.domain.model.LanguageId

interface LanguageRepository {
    fun findById(id: LanguageId): Language?

    fun findAll(): List<Language>
}
