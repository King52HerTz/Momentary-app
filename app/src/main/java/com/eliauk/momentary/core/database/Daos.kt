package com.eliauk.momentary.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface MomentDao {
    @Query("SELECT * FROM moments WHERE localDate = :localDate ORDER BY recordedAt ASC")
    fun observeByDate(localDate: LocalDate): Flow<List<MomentEntity>>

    @Query("SELECT * FROM moments WHERE id IN (:ids)")
    suspend fun findByIds(ids: List<String>): List<MomentEntity>

    @Upsert
    suspend fun upsert(moment: MomentEntity)

    @Upsert
    suspend fun upsertAll(moments: List<MomentEntity>)

    @Delete
    suspend fun delete(moment: MomentEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun observeConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun findById(id: String): ChatMessageEntity?

    @Upsert
    suspend fun upsert(message: ChatMessageEntity)

    @Upsert
    suspend fun upsertAll(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_messages SET status = :status WHERE id = :messageId")
    suspend fun updateStatus(messageId: String, status: String): Int
}

@Dao
interface PersonaDao {
    @Query("SELECT * FROM personas WHERE isArchived = 0 ORDER BY isBuiltIn DESC, createdAt ASC")
    fun observeAvailable(): Flow<List<PersonaEntity>>

    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun findById(id: String): PersonaEntity?

    @Upsert
    suspend fun upsert(persona: PersonaEntity)

    @Upsert
    suspend fun upsertAll(personas: List<PersonaEntity>)
}

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_sessions WHERE localDate = :localDate")
    fun observeSession(localDate: LocalDate): Flow<DiarySessionEntity?>

    @Query("SELECT * FROM diary_sessions WHERE localDate = :localDate")
    suspend fun findSession(localDate: LocalDate): DiarySessionEntity?

    @Upsert
    suspend fun upsertSession(session: DiarySessionEntity)

    @Query("DELETE FROM diary_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String): Int

    @Insert
    suspend fun insertSources(sources: List<DiarySessionSourceEntity>)

    @Query("DELETE FROM diary_session_sources WHERE sessionId = :sessionId")
    suspend fun deleteSources(sessionId: String): Int

    @Transaction
    suspend fun replaceSources(
        sessionId: String,
        sources: List<DiarySessionSourceEntity>,
    ) {
        require(sources.all { it.sessionId == sessionId }) {
            "Every source must belong to the session being replaced"
        }
        deleteSources(sessionId)
        insertSources(sources)
    }

    @Query("SELECT momentId FROM diary_session_sources WHERE sessionId = :sessionId ORDER BY position ASC")
    suspend fun getSourceMomentIds(sessionId: String): List<String>

    @Query("SELECT COUNT(*) FROM diary_session_sources WHERE sessionId = :sessionId")
    suspend fun countSources(sessionId: String): Int

    @Upsert
    suspend fun upsertDraft(draft: DiaryDraftEntity)

    @Query("SELECT * FROM diary_drafts WHERE sessionId = :sessionId ORDER BY revision DESC")
    fun observeDrafts(sessionId: String): Flow<List<DiaryDraftEntity>>

    @Query("SELECT * FROM diary_drafts WHERE sessionId = :sessionId ORDER BY revision DESC LIMIT 1")
    suspend fun findLatestDraft(sessionId: String): DiaryDraftEntity?

    @Upsert
    suspend fun upsertJournal(journal: JournalEntity)

    @Query("SELECT * FROM journals WHERE localDate = :localDate")
    fun observeJournal(localDate: LocalDate): Flow<JournalEntity?>

    @Query("SELECT * FROM journals ORDER BY localDate DESC")
    fun observeJournals(): Flow<List<JournalEntity>>
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Upsert
    suspend fun upsert(favorite: FavoriteEntity)

    @Delete
    suspend fun delete(favorite: FavoriteEntity)
}
