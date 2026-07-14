package com.eliauk.momentary.data

import com.eliauk.momentary.model.AiPersona
import com.eliauk.momentary.model.PersonaDefaults
import org.junit.Assert.assertTrue
import org.junit.Test

class PrototypeAiServiceTest {
    private val service = PrototypeAiService()
    private val momentary = PersonaDefaults.all().first { it.id == PersonaDefaults.MOMENTARY_ID }

    @Test
    fun `tired message receives a listening response`() {
        val response = service.replyTo("最近真的很累", momentary)

        assertTrue(response.contains("精力"))
        assertTrue(response.contains("照顾"))
    }

    @Test
    fun `score message avoids defining the user`() {
        val response = service.replyTo("这次考试成绩不太好", momentary)

        assertTrue(response.contains("没有资格"))
        assertTrue(response.contains("定义"))
    }

    @Test
    fun `story persona uses a narrative response`() {
        val story = PersonaDefaults.all().first { it.id == PersonaDefaults.STORY_ID }

        val response = service.replyTo("最近真的很累", story)

        assertTrue(response.contains("鞋底"))
        assertTrue(response.contains("路"))
    }

    @Test
    fun `custom persona keeps its response preference visible`() {
        val custom = AiPersona("custom", "小灯", "陪我整理想法", "简短、坚定、少说教")

        val response = service.replyTo("今天有点乱", custom)

        assertTrue(response.contains("简短、坚定、少说教"))
    }
}
