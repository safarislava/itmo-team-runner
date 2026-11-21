package ru.ifmo.se.runner.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.runner.domain.model.Language
import ru.ifmo.se.runner.domain.model.LanguageId
import ru.ifmo.se.runner.domain.repository.LanguageRepository
import ru.ifmo.se.runner.infrastructure.postgres.mapper.LanguageEntityMapper
import ru.ifmo.se.runner.infrastructure.postgres.repository.JpaLanguageRepository

@Repository
class LanguageRepositoryImpl(
    private val repo: JpaLanguageRepository,
    private val mapper: LanguageEntityMapper,
) : LanguageRepository {
    override fun findById(id: LanguageId): Language? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(): List<Language> = repo.findAll().map(mapper::toDomain)
}
