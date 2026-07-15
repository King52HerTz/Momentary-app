package com.eliauk.momentary.data

import com.eliauk.momentary.model.AiPersona
import com.eliauk.momentary.model.ChatMessage
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

class AiGatewayException(
    val userMessage: String,
    cause: Throwable? = null,
) : IOException(userMessage, cause)

class RemoteAiCompanionService(
    gatewayUrl: String,
    private val connectionFactory: (URL) -> HttpURLConnection = { url ->
        url.openConnection() as HttpURLConnection
    },
) : AiCompanionService {
    private val endpoint = resolveEndpoint(gatewayUrl)

    override fun replyTo(
        message: String,
        persona: AiPersona,
        context: List<ChatMessage>,
    ): String {
        val url = endpoint ?: throw AiGatewayException("AI 服务尚未配置，请先设置网关地址")
        val connection = connectionFactory(url)
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.outputStream.use { output ->
                output.write(buildRequest(message, persona, context).toByteArray(Charsets.UTF_8))
            }

            val status = connection.responseCode
            if (status !in 200..299) {
                throw AiGatewayException(
                    if (status == 401 || status == 403) "AI 服务认证失败，请检查服务端配置"
                    else "AI 服务暂时不可用，请稍后再试",
                )
            }
            val response = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            JSONObject(response).optString("text").trim()
                .takeIf { it.isNotEmpty() }
                ?: throw AiGatewayException("AI 返回了空内容，请重试")
        } catch (error: AiGatewayException) {
            throw error
        } catch (error: Exception) {
            throw AiGatewayException("无法连接 AI 服务，请检查网络后重试", error)
        } finally {
            connection.disconnect()
        }
    }

    private fun buildRequest(
        message: String,
        persona: AiPersona,
        context: List<ChatMessage>,
    ): String {
        val messages = JSONArray()
        context
            .filter { it.personaId == persona.id && it.text.isNotBlank() }
            .takeLast(MAX_CONTEXT_MESSAGES)
            .forEach { item ->
                messages.put(
                    JSONObject()
                        .put("role", if (item.isUser) "user" else "assistant")
                        .put("content", item.text),
                )
            }
        messages.put(JSONObject().put("role", "user").put("content", message))

        return JSONObject()
            .put("mode", "companion")
            .put(
                "persona",
                JSONObject()
                    .put("name", persona.name)
                    .put("description", persona.description)
                    .put("responseStyle", persona.responseStyle),
            )
            .put("messages", messages)
            .toString()
    }

    private fun resolveEndpoint(gatewayUrl: String): URL? {
        val value = gatewayUrl.trim().trimEnd('/')
        if (value.isEmpty()) return null
        val uri = runCatching { URI(value) }.getOrNull()
            ?: throw IllegalArgumentException("AI gateway URL is invalid")
        require(uri.scheme == "https" || isLocalDebugUri(uri)) {
            "AI gateway must use HTTPS outside local debug environments"
        }
        return URI("$value/api/chat").toURL()
    }

    private fun isLocalDebugUri(uri: URI): Boolean =
        uri.scheme == "http" && uri.host in setOf("10.0.2.2", "127.0.0.1", "localhost")

    private companion object {
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 45_000
        const val MAX_CONTEXT_MESSAGES = 20
    }
}
