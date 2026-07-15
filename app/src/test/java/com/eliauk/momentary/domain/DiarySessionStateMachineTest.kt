package com.eliauk.momentary.domain

import com.eliauk.momentary.model.DiarySession
import com.eliauk.momentary.model.DiarySessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DiarySessionStateMachineTest {
    private val date = "2026-07-14"

    @Test
    fun `session follows the complete diary workflow`() {
        val idle = DiarySessionStateMachine.forDate(null, date)
        val collecting = DiarySessionStateMachine.start(idle, now = 100L)
        val withEntry = DiarySessionStateMachine.collect(collecting, entryId = 42L)
        val generating = DiarySessionStateMachine.beginGeneration(withEntry)
        val reviewing = DiarySessionStateMachine.markReviewing(generating)
        val completed = DiarySessionStateMachine.complete(reviewing, now = 200L)

        assertEquals(DiarySessionState.COMPLETED, completed.state)
        assertEquals(listOf(42L), completed.entryIds)
        assertEquals(100L, completed.startedAt)
        assertEquals(200L, completed.completedAt)
    }

    @Test
    fun `same date restores the existing collecting session`() {
        val collecting = DiarySession(
            localDate = date,
            state = DiarySessionState.COLLECTING,
            startedAt = 100L,
            entryIds = listOf(7L),
        )

        assertEquals(collecting, DiarySessionStateMachine.forDate(collecting, date))
    }

    @Test
    fun `new date starts idle without carrying yesterday entries`() {
        val yesterday = DiarySession(
            localDate = "2026-07-13",
            state = DiarySessionState.COLLECTING,
            entryIds = listOf(7L),
        )

        val today = DiarySessionStateMachine.forDate(yesterday, date)

        assertEquals(DiarySessionState.IDLE, today.state)
        assertEquals(date, today.localDate)
        assertEquals(emptyList<Long>(), today.entryIds)
    }

    @Test
    fun `duplicate entry id is not collected twice`() {
        val collecting = DiarySessionStateMachine.start(DiarySession(date), now = 100L)
        val once = DiarySessionStateMachine.collect(collecting, entryId = 42L)

        assertEquals(once, DiarySessionStateMachine.collect(once, entryId = 42L))
    }

    @Test
    fun `illegal transition is rejected`() {
        assertThrows(IllegalStateException::class.java) {
            DiarySessionStateMachine.beginGeneration(DiarySession(date))
        }
    }
}
