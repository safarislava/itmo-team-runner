package ru.ifmo.se.runner.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.runner.domain.model.Language
import ru.ifmo.se.runner.domain.model.LanguageId
import ru.ifmo.se.runner.infrastructure.postgres.entity.LanguageEntity

@Component
class LanguageEntityMapper {
    fun toDomain(entity: LanguageEntity): Language =
        Language(
            id = LanguageId(entity.id),
            name = entity.name,
            version = entity.version,
        )

    fun toEntity(domain: Language): LanguageEntity =
        LanguageEntity(
            id = domain.id.value,
            name = domain.name,
            version = domain.version,
        )
}
