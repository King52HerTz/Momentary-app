package com.eliauk.momentary.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MomentaryDatabaseTest {
    private lateinit var database: MomentaryDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MomentaryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun momentsAreObservedByLocalDateInRecordedOrder() = runBlocking {
        val date = LocalDate.of(2026, 7, 15)
        database.momentDao().upsertAll(
            listOf(
                moment("later", date, "2026-07-15T12:00:00Z"),
                moment("tomorrow", date.plusDays(1), "2026-07-16T08:00:00Z"),
                moment("earlier", date, "2026-07-15T08:00:00Z"),
            )
        )

        val moments = database.momentDao().observeByDate(date).first()

        assertEquals(listOf("earlier", "later"), moments.map { it.id })
    }

    @Test
    fun sessionSourcesKeepOrderAndCascadeWithSession() = runBlocking {
        val date = LocalDate.of(2026, 7, 15)
        database.momentDao().upsertAll(
            listOf(
                moment("moment-1", date, "2026-07-15T08:00:00Z"),
                moment("moment-2", date, "2026-07-15T09:00:00Z"),
            )
        )
        database.diaryDao().upsertSession(session("session-1", date))
        database.diaryDao().insertSources(
            listOf(
                source("session-1", "moment-2", position = 1),
                source("session-1", "moment-1", position = 0),
            )
        )

        assertEquals(
            listOf("moment-1", "moment-2"),
            database.diaryDao().getSourceMomentIds("session-1"),
        )

        database.diaryDao().deleteSession("session-1")

        assertEquals(0, database.diaryDao().countSources("session-1"))
        assertNull(database.diaryDao().findSession(date))
    }

    @Test
    fun latestDraftUsesHighestRevision() = runBlocking {
        val date = LocalDate.of(2026, 7, 15)
        database.diaryDao().upsertSession(session("session-1", date))
        database.diaryDao().upsertDraft(draft("draft-1", "session-1", revision = 1))
        database.diaryDao().upsertDraft(draft("draft-2", "session-1", revision = 2))

        val latest = database.diaryDao().findLatestDraft("session-1")

        assertEquals("draft-2", latest?.id)
        assertEquals(2, latest?.revision)
    }

    @Test
    fun deletingIncludedMomentKeepsChatMessageAndClearsReference() = runBlocking {
        val date = LocalDate.of(2026, 7, 15)
        val includedMoment = moment("moment-1", date, "2026-07-15T08:00:00Z")
        database.momentDao().upsert(includedMoment)
        database.chatMessageDao().upsert(
            ChatMessageEntity(
                id = "message-1",
                conversationId = "conversation-1",
                personaId = "persona-1",
                role = "ASSISTANT",
                content = "A helpful reply",
                status = "SENT",
                createdAt = Instant.parse("2026-07-15T08:01:00Z"),
                includedMomentId = includedMoment.id,
            )
        )

        database.momentDao().delete(includedMoment)

        val preservedMessage = database.chatMessageDao().findById("message-1")
        assertEquals("A helpful reply", preservedMessage?.content)
        assertNull(preservedMessage?.includedMomentId)
    }

    private fun moment(id: String, date: LocalDate, recordedAt: String) = MomentEntity(
        id = id,
        localDate = date,
        recordedAt = Instant.parse(recordedAt),
        content = "content-$id",
        source = "MANUAL",
        createdAt = Instant.parse(recordedAt),
    )

    private fun session(id: String, date: LocalDate) = DiarySessionEntity(
        id = id,
        localDate = date,
        state = "COLLECTING",
        startedAt = Instant.parse("2026-07-15T08:00:00Z"),
        createdAt = Instant.parse("2026-07-15T08:00:00Z"),
        updatedAt = Instant.parse("2026-07-15T08:00:00Z"),
    )

    private fun source(sessionId: String, momentId: String, position: Int) =
        DiarySessionSourceEntity(
            sessionId = sessionId,
            momentId = momentId,
            position = position,
            addedAt = Instant.parse("2026-07-15T10:00:00Z"),
        )

    private fun draft(id: String, sessionId: String, revision: Int) = DiaryDraftEntity(
        id = id,
        sessionId = sessionId,
        revision = revision,
        schemaVersion = 1,
        title = "标题 $revision",
        body = "正文 $revision",
        eventsJson = "[]",
        moodAndState = "平静",
        inspirationsJson = "[]",
        wishesJson = "[]",
        gratitudeJson = "[]",
        todosJson = "[]",
        insight = "",
        generatedAt = Instant.parse("2026-07-15T11:00:00Z").plusSeconds(revision.toLong()),
        updatedAt = Instant.parse("2026-07-15T11:00:00Z").plusSeconds(revision.toLong()),
    )
}
