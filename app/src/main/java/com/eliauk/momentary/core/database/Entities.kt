package com.eliauk.momentary.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "moments",
    indices = [
        Index(value = ["localDate"]),
        Index(value = ["recordedAt"]),
    ],
)
data class MomentEntity(
    @androidx.room.PrimaryKey val id: String,
    val localDate: LocalDate,
    val recordedAt: Instant,
    val content: String,
    val title: String? = null,
    val source: String,
    val isFavorite: Boolean = false,
    val createdAt: Instant,
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = MomentEntity::class,
            parentColumns = ["id"],
            childColumns = ["includedMomentId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["conversationId", "createdAt"]),
        Index(value = ["personaId"]),
        Index(value = ["includedMomentId"]),
    ],
)
data class ChatMessageEntity(
    @androidx.room.PrimaryKey val id: String,
    val conversationId: String,
    val personaId: String,
    val role: String,
    val content: String,
    val status: String,
    val createdAt: Instant,
    val isFavorite: Boolean = false,
    val includedMomentId: String? = null,
)

@Entity(
    tableName = "personas",
    indices = [Index(value = ["isBuiltIn", "isArchived"])],
)
data class PersonaEntity(
    @androidx.room.PrimaryKey val id: String,
    val name: String,
    val description: String,
    val responseStyle: String,
    val isBuiltIn: Boolean,
    val isArchived: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Entity(
    tableName = "diary_sessions",
    indices = [
        Index(value = ["localDate"], unique = true),
        Index(value = ["generationRequestId"], unique = true),
    ],
)
data class DiarySessionEntity(
    @androidx.room.PrimaryKey val id: String,
    val localDate: LocalDate,
    val state: String,
    val startedAt: Instant? = null,
    val completedAt: Instant? = null,
    val generationRequestId: String? = null,
    val lastErrorCode: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Entity(
    tableName = "diary_session_sources",
    primaryKeys = ["sessionId", "momentId"],
    foreignKeys = [
        ForeignKey(
            entity = DiarySessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MomentEntity::class,
            parentColumns = ["id"],
            childColumns = ["momentId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["sessionId", "position"], unique = true),
        Index(value = ["momentId"]),
    ],
)
data class DiarySessionSourceEntity(
    val sessionId: String,
    val momentId: String,
    val position: Int,
    val addedAt: Instant,
)

@Entity(
    tableName = "diary_drafts",
    foreignKeys = [
        ForeignKey(
            entity = DiarySessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["sessionId", "revision"], unique = true),
    ],
)
data class DiaryDraftEntity(
    @androidx.room.PrimaryKey val id: String,
    val sessionId: String,
    val revision: Int,
    val schemaVersion: Int,
    val title: String,
    val body: String,
    val eventsJson: String,
    val moodAndState: String,
    val inspirationsJson: String,
    val wishesJson: String,
    val gratitudeJson: String,
    val todosJson: String,
    val insight: String,
    val generatedAt: Instant,
    val updatedAt: Instant,
)

@Entity(
    tableName = "journals",
    indices = [
        Index(value = ["localDate"], unique = true),
        Index(value = ["draftId"]),
    ],
)
data class JournalEntity(
    @androidx.room.PrimaryKey val id: String,
    val localDate: LocalDate,
    val draftId: String,
    val title: String,
    val body: String,
    val summaryJson: String,
    val isFavorite: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Entity(
    tableName = "favorites",
    indices = [
        Index(value = ["targetType", "targetId"], unique = true),
        Index(value = ["createdAt"]),
    ],
)
data class FavoriteEntity(
    @androidx.room.PrimaryKey val id: String,
    val targetType: String,
    val targetId: String,
    val contentSnapshot: String,
    val createdAt: Instant,
)
