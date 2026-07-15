# Momentary API 契约 v1

本文档是 Android 与后端共同遵守的唯一接口定义。任何一方修改字段、路径或错误语义前，必须先修改本文档。

状态：**目标契约已冻结，尚未由正式后端和 Android v1 网络层实现。** 当前运行的 `/health` 与 `/api/chat` 仍是临时联调接口。

## 1. 基本约定

- Base path：`/api/v1`
- 生产协议：HTTPS
- Content-Type：`application/json; charset=utf-8`
- 时间：ISO 8601 UTC，例如 `2026-07-15T11:20:30Z`
- 本地日期：`YYYY-MM-DD`
- ID：客户端生成 UUID 字符串
- 文本编码：UTF-8
- API Key：只存在后端到模型供应商的调用中

每个响应返回请求标识：

```http
X-Request-Id: 2c72f4a6-5bd0-4a45-97f2-327bbc1d117d
```

## 2. 通用错误

```json
{
  "error": {
    "code": "AI_PROVIDER_UNAVAILABLE",
    "message": "AI 服务暂时不可用，请稍后再试",
    "requestId": "2c72f4a6-5bd0-4a45-97f2-327bbc1d117d",
    "retryable": true
  }
}
```

| HTTP | code | retryable | 含义 |
| --- | --- | --- | --- |
| 400 | `INVALID_REQUEST` | false | 字段缺失、格式或长度错误 |
| 401 | `UNAUTHORIZED` | false | 未来账号版身份失效 |
| 409 | `REQUEST_CONFLICT` | false | 幂等键与既有请求冲突 |
| 413 | `PAYLOAD_TOO_LARGE` | false | 请求超过限制 |
| 429 | `RATE_LIMITED` | true | 调用频率或额度超限 |
| 502 | `AI_PROVIDER_UNAVAILABLE` | true | 上游模型失败 |
| 502 | `AI_INVALID_OUTPUT` | true | 模型输出无法通过结构校验 |
| 504 | `AI_PROVIDER_TIMEOUT` | true | 上游超时 |
| 500 | `INTERNAL_ERROR` | true | 未预期服务端错误 |

后端不能把模型供应商响应正文、API Key、堆栈或内部模型名称返回客户端。

## 3. 健康检查

```http
GET /api/v1/health
```

```json
{
  "status": "ok",
  "apiVersion": "v1"
}
```

健康检查不调用模型供应商。

## 4. 陪伴聊天

```http
POST /api/v1/chat/completions
Idempotency-Key: 785f82c0-6f42-4971-a3c7-cd97af43fc65
```

### 请求

```json
{
  "conversationId": "8f07e11c-0b8f-4c76-b203-ff64d700a3fa",
  "persona": {
    "id": "momentary",
    "name": "片刻",
    "description": "先听懂感受，再陪用户慢慢往下说",
    "responseStyle": "温柔、克制、少建议"
  },
  "messages": [
    {
      "id": "b8440914-31f4-4f30-83d7-72631ee4f0c7",
      "role": "user",
      "content": "最近总觉得有点累。",
      "createdAt": "2026-07-15T11:20:30Z"
    }
  ]
}
```

约束：

- `messages` 为 1 到 20 条，最后一条必须是 `user`；
- `role` 只允许 `user`、`assistant`；
- 单条 `content` 1 到 4000 个 Unicode 字符；
- `persona.name` 最多 30 字，说明和风格各最多 300 字；
- 后端系统安全规则不能由客户端覆盖；
- 客户端只发送当前角色需要的最近上下文。

### 响应

```json
{
  "message": {
    "id": "3ad3507a-b773-413e-a286-506179325513",
    "role": "assistant",
    "content": "听起来你最近一直在消耗精力。今天最想先放下的是哪一件事？",
    "createdAt": "2026-07-15T11:20:32Z"
  },
  "requestId": "2c72f4a6-5bd0-4a45-97f2-327bbc1d117d"
}
```

客户端保存自己的消息 ID；服务端不保存完整对话历史，除非未来账号版另行更新隐私和接口契约。

## 5. 生成日记草稿

```http
POST /api/v1/diary-drafts:generate
Idempotency-Key: a5bc15b6-5a9b-4ed4-a040-3436b5c5c6c4
```

### 请求

```json
{
  "sessionId": "3212049a-a3b2-4b97-b0b7-a6377fc35cbe",
  "localDate": "2026-07-15",
  "timezone": "Asia/Shanghai",
  "mood": "疲惫",
  "sources": [
    {
      "id": "a04ce070-2dfd-4387-8aaf-62fc541cb265",
      "type": "MOMENT",
      "content": "数据库考了64分，有点不甘心。",
      "recordedAt": "2026-07-15T08:20:00Z"
    },
    {
      "id": "45a5a802-1a4a-4e5d-b82f-37376b049ddc",
      "type": "CHAT_ASSISTANT",
      "content": "一次成绩不能替你定义整个人。",
      "recordedAt": "2026-07-15T08:25:00Z"
    }
  ],
  "preferences": {
    "tone": "自然、克制、保留原意",
    "includeInsight": true
  }
}
```

约束：

- `sources` 为 1 到 100 条；
- 单条内容最多 6000 字，总内容最多 30000 字；
- `type` 只允许 `MOMENT`、`CHAT_USER`、`CHAT_ASSISTANT`；
- 后端不得接受客户端传入系统 Prompt；
- 同一个 `Idempotency-Key` 必须返回同一份结果或明确冲突。

### 响应

```json
{
  "draft": {
    "id": "4d634490-2abf-485d-b94d-f23a87c225a6",
    "sessionId": "3212049a-a3b2-4b97-b0b7-a6377fc35cbe",
    "revision": 1,
    "title": "成绩出来后的那个下午",
    "body": "数据库成绩出来了，64分。我有些不甘心……",
    "events": [
      {
        "text": "数据库成绩为64分",
        "sourceIds": ["a04ce070-2dfd-4387-8aaf-62fc541cb265"]
      }
    ],
    "moodAndState": "失落、不甘心，也在慢慢把一次结果与自我评价分开。",
    "inspirations": [
      {
        "text": "一次成绩不能定义完整的自己",
        "sourceIds": [
          "a04ce070-2dfd-4387-8aaf-62fc541cb265",
          "45a5a802-1a4a-4e5d-b82f-37376b049ddc"
        ]
      }
    ],
    "wishes": ["希望更平静地看待一次考试结果"],
    "gratitude": [],
    "todos": [
      {
        "id": "9fd53fe8-aa3b-47a5-b23f-3c97e4c8af42",
        "text": "整理数据库考试中的薄弱知识点",
        "completed": false
      }
    ],
    "insight": "你在意的不只是分数，也是在意他人会如何评价你。可以先把事实与推测分开。",
    "sourceIds": [
      "a04ce070-2dfd-4387-8aaf-62fc541cb265",
      "45a5a802-1a4a-4e5d-b82f-37376b049ddc"
    ],
    "generatedAt": "2026-07-15T13:00:00Z"
  },
  "requestId": "31d471a4-eb63-4fc1-a7d5-cbf42cd41c7b"
}
```

### 响应校验

- `title`、`body`、`moodAndState` 必须存在；
- 所有 `sourceIds` 必须来自请求；
- `events` 和 `inspirations` 的来源不能为空；
- `todos` 每项包含稳定 ID、文本和完成状态；
- 后端最多进行一次格式修复重试；
- 仍不合法时返回 `AI_INVALID_OUTPUT`，不得返回半份草稿。

## 6. 暂不提供的接口

v1 不提供账号、云同步、服务端日记 CRUD、服务端图片导出和长期记忆接口。正式日记仍由 Android 本地保存。

## 7. 旧接口迁移

当前本地验证接口 `/health` 和 `/api/chat` 标记为 `legacy`。正式后端实现 v1 后：

1. Android 切换到 v1 DTO；
2. 完成契约测试和端到端验证；
3. 删除旧客户端调用；
4. 删除后端旧路径，不长期双轨维护。
