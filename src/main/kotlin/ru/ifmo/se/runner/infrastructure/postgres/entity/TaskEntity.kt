package ru.ifmo.se.runner.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "task")
class TaskEntity(
    @Id
    @Column(name = "task_id")
    override var id: UUID,
    @Column(name = "author_id")
    var authorId: UUID,
    var name: String,
    var description: String?,
    var timeLimit: Int,
    var memoryLimit: Int,
) : BasicEntity()
