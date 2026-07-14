package com.eliauk.momentary.data

import com.eliauk.momentary.model.AiPersona
import com.eliauk.momentary.model.PersonaDefaults

interface AiCompanionService {
    fun replyTo(message: String, persona: AiPersona): String
}

class PrototypeAiService : AiCompanionService {
    override fun replyTo(message: String, persona: AiPersona): String {
        val core = when {
        "累" in message || "疲" in message ->
            "听起来你最近消耗了很多精力。我们可以先不急着解决全部事情，只看看此刻最需要被照顾的那一部分。"
        "考试" in message || "成绩" in message ->
            "一次成绩能说明这次答题的结果，却没有资格替你定义整个人。先允许失落停一会儿，再决定下一步要带走什么。"
        "开心" in message || "高兴" in message ->
            "真好。把这份开心写具体一点吧，未来的你会更容易循着这些细节找到今天。"
        else ->
            "我收到了。愿意再多说一点吗？今天这件事里，哪一小部分最牵动你？"
        }
        return when (persona.id) {
            PersonaDefaults.STORY_ID -> storyReply(message)
            PersonaDefaults.CLARITY_ID -> clarityReply(message)
            PersonaDefaults.MOMENTARY_ID -> core
            else -> "$core\n\n我会记得你希望我的回应是：${persona.responseStyle}。"
        }
    }

    private fun storyReply(message: String): String = when {
        "累" in message || "疲" in message ->
            "你像是走了很久，鞋底还沾着今天的尘土。先别急着赶下一段路，和我说说，哪一刻让你最想停下来？"
        "考试" in message || "成绩" in message ->
            "分数像一张匆忙盖下的印章，只落在一页纸上，不会盖住你整本故事。失落可以留在这一章，但它不是结尾。"
        else ->
            "这件事里一定藏着一个很具体的瞬间。是一个表情、一句话，还是回家路上的某束光？从那里讲起吧。"
    }

    private fun clarityReply(message: String): String = when {
        "累" in message || "疲" in message ->
            "先分三件事：你已经完成了什么、什么正在消耗你、今晚最小的恢复动作是什么。我们可以从第二项开始。"
        "考试" in message || "成绩" in message ->
            "事实是这次结果没有达到预期；感受可能是失落或担心；下一步只需要找出一个最值得修正的环节。结果不等于能力定论。"
        else ->
            "我们先把它拆开：发生了什么、你感受到什么、你现在最希望改变什么？先回答其中最容易的一项。"
    }
}
