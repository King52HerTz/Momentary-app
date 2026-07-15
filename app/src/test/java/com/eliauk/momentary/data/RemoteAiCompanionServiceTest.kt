package com.eliauk.momentary.data

import com.eliauk.momentary.model.PersonaDefaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class RemoteAiCompanionServiceTest {
    private val persona = PersonaDefaults.all().first()

    @Test
    fun `missing gateway reports a configuration error before network access`() {
        val service = RemoteAiCompanionService("")

        val error = assertThrows(AiGatewayException::class.java) {
            service.replyTo("今天有点累", persona)
        }

        assertEquals("AI 服务尚未配置，请先设置网关地址", error.userMessage)
    }

    @Test
    fun `plain http is rejected for non-local gateway`() {
        assertThrows(IllegalArgumentException::class.java) {
            RemoteAiCompanionService("http://example.com")
        }
    }
}
