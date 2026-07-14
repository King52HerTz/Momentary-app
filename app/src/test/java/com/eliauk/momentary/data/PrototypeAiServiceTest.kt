package com.eliauk.momentary.data

import org.junit.Assert.assertTrue
import org.junit.Test

class PrototypeAiServiceTest {
    private val service = PrototypeAiService()

    @Test
    fun `tired message receives a listening response`() {
        val response = service.replyTo("最近真的很累")

        assertTrue(response.contains("精力"))
        assertTrue(response.contains("照顾"))
    }

    @Test
    fun `score message avoids defining the user`() {
        val response = service.replyTo("这次考试成绩不太好")

        assertTrue(response.contains("没有资格"))
        assertTrue(response.contains("定义"))
    }
}
