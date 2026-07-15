package com.eliauk.momentary.domain

import com.eliauk.momentary.model.DiarySession
import com.eliauk.momentary.model.DiarySessionState

object DiarySessionStateMachine {
    fun forDate(session: DiarySession?, localDate: String): DiarySession {
        return session?.takeIf { it.localDate == localDate } ?: DiarySession(localDate = localDate)
    }

    fun start(session: DiarySession, now: Long): DiarySession {
        session.requireState(DiarySessionState.IDLE)
        return session.copy(
            state = DiarySessionState.COLLECTING,
            startedAt = now,
            completedAt = null,
            entryIds = emptyList(),
        )
    }

    fun collect(session: DiarySession, entryId: Long): DiarySession {
        session.requireState(DiarySessionState.COLLECTING)
        if (entryId in session.entryIds) return session
        return session.copy(entryIds = session.entryIds + entryId)
    }

    fun beginGeneration(session: DiarySession): DiarySession {
        session.requireState(DiarySessionState.COLLECTING)
        return session.copy(state = DiarySessionState.GENERATING)
    }

    fun markReviewing(session: DiarySession): DiarySession {
        session.requireState(DiarySessionState.GENERATING)
        return session.copy(state = DiarySessionState.REVIEWING)
    }

    fun complete(session: DiarySession, now: Long): DiarySession {
        session.requireState(DiarySessionState.REVIEWING)
        return session.copy(state = DiarySessionState.COMPLETED, completedAt = now)
    }

    private fun DiarySession.requireState(expected: DiarySessionState) {
        check(state == expected) {
            "Diary session must be $expected before this transition, but was $state"
        }
    }
}
