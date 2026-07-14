package com.eliauk.momentary.data

interface AiCompanionService {
    fun replyTo(message: String): String
}

class PrototypeAiService : AiCompanionService {
    override fun replyTo(message: String): String = when {
        "累" in message || "疲" in message ->
            "听起来你最近消耗了很多精力。我们可以先不急着解决全部事情，只看看此刻最需要被照顾的那一部分。"
        "考试" in message || "成绩" in message ->
            "一次成绩能说明这次答题的结果，却没有资格替你定义整个人。先允许失落停一会儿，再决定下一步要带走什么。"
        "开心" in message || "高兴" in message ->
            "真好。把这份开心写具体一点吧，未来的你会更容易循着这些细节找到今天。"
        else ->
            "我收到了。愿意再多说一点吗？今天这件事里，哪一小部分最牵动你？"
    }
}
