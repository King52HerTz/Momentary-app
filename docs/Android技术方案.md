# Android 技术方案

状态：目标架构。当前代码正在从单 ViewModel + SharedPreferences JSON 逐步迁移，不能把本文描述误认为已经完成。

## 1. 技术基线

| 项目 | 选择 |
| --- | --- |
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 导航 | Navigation Compose |
| 异步 | Kotlin Coroutines + Flow |
| 本地数据库 | Room |
| 简单设置 | DataStore Preferences |
| 网络 | Retrofit + OkHttp + Kotlin Serialization |
| 依赖注入 | 第一版使用显式 `AppContainer`，暂不引入 Hilt |
| 图片加载 | 需要图片记录时再引入 Coil |
| 测试 | JUnit、kotlinx-coroutines-test、Room Test、Compose UI Test |

不更换 Flutter，不做 Compose Multiplatform。先把 Android 第一版做完整。

## 2. 目标架构

```text
Compose Screen
    ↓ events              ↑ UiState
Feature ViewModel
    ↓
Use Case / Domain Service
    ↓
Repository interface
    ├── Local data source: Room / DataStore
    └── Remote data source: Momentary API
```

规则：

- Compose 只渲染状态和发送事件，不直接读数据库或发网络请求；
- ViewModel 只编排当前页面用例，不负责 JSON、SQL 或 HTTP 细节；
- UseCase 保存跨页面业务规则，例如日记结束、生成、装订；
- Repository 是客户端数据边界，决定本地优先和远程调用顺序；
- Remote DTO、Room Entity 与 Domain Model 分开，禁止复用一个类贯穿所有层。

## 3. 建议目录

```text
com.eliauk.momentary
├── app
│   ├── MomentaryApplication.kt
│   ├── AppContainer.kt
│   └── navigation/
├── core
│   ├── database/
│   ├── datastore/
│   ├── network/
│   ├── designsystem/
│   └── common/
├── feature
│   ├── today/
│   │   ├── data/
│   │   ├── domain/
│   │   └── ui/
│   ├── chat/
│   ├── diary/
│   ├── journal/
│   ├── echo/
│   └── settings/
└── model/
```

第一轮重构仍保持单 Gradle module，等编译时间或团队规模确实需要时再拆 module。

## 4. 状态管理

每个顶层 feature 拥有独立 ViewModel 和不可变 `UiState`：

```kotlin
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val activePersona: Persona? = null,
    val sendState: SendState = SendState.Idle,
)

sealed interface SendState {
    data object Idle : SendState
    data class Sending(val clientMessageId: String) : SendState
    data class Failed(val clientMessageId: String, val message: String) : SendState
}
```

- 短暂 UI 状态留在 Composable，例如弹窗是否展开；
- 会影响业务恢复的状态进入 ViewModel/数据库；
- 一次性事件使用明确事件流或导航回调，不把 Toast 文本永久塞进 UiState；
- 禁止继续扩大全局 `MomentaryUiState`。

## 5. 领域模型

核心模型建议统一使用稳定 ID 和机器可解析时间：

```text
Moment
  id: String (UUID)
  localDate: LocalDate
  recordedAt: Instant
  content: String
  source: MANUAL | CHAT_USER | CHAT_ASSISTANT
  favorite: Boolean

DiarySession
  id: String
  localDate: LocalDate
  state: IDLE | COLLECTING | GENERATING | REVIEWING | COMPLETED
  startedAt: Instant?
  completedAt: Instant?

DiaryDraft
  id: String
  sessionId: String
  revision: Int
  title: String
  body: String
  events: List<SourceBackedText>
  moodAndState: String
  inspirations: List<SourceBackedText>
  wishes: List<String>
  gratitude: List<String>
  todos: List<TodoItem>
  insight: String
  generatedAt: Instant

Journal
  id: String
  localDate: LocalDate
  draftId: String
  title: String
  body: String
  createdAt: Instant
  updatedAt: Instant
```

展示日期只能由 formatter 生成，不能作为数据主键。

## 6. Room 数据设计

第一版表：

- `moments`：原始片刻与收录内容；
- `chat_messages`：角色对话消息和发送状态；
- `personas`：内置与自定义角色配置；
- `diary_sessions`：每天的采集状态；
- `diary_session_sources`：会话与片刻的多对多关系；
- `diary_drafts`：AI 草稿及其 revision；
- `journals`：用户确认后的正式日记；
- `favorites`：统一收藏引用。

结构化列表可先作为 JSON 列存入 `diary_drafts`，但必须有 schema version 和 converter 测试。来源关系必须单独建表，不能只埋在 JSON 中。

### SharedPreferences 迁移

1. Room 首次创建后检查 `legacy_migration_completed`；
2. 在单个事务中解析旧 JSON 并写入新表；
3. 迁移成功后设置标记，但暂时保留旧数据一个版本；
4. 解析失败时保留原字符串并报告可恢复错误，不能静默返回空列表；
5. 下一大版本再删除旧键。

DataStore 只保存主题、当前角色 ID、提醒设置和迁移标记。

## 7. 网络层

- Base URL 由构建参数注入，Release 只允许 HTTPS；
- API Key 永远不在客户端；
- 所有接口遵循 [API 契约 v1](API契约-v1.md)；
- OkHttp 统一设置连接、读取和调用超时；
- 只记录 request ID、状态码和耗时，不记录日记正文；
- RemoteDataSource 将 HTTP/JSON 错误转换成领域错误；
- Repository 决定重试和本地状态恢复，UI 不解析状态码。

聊天第一版先用普通请求。只有闭环稳定且确实需要逐字体验时，再引入 SSE，不同时开发两种协议。

## 8. 日记生成流程

```text
EndDiaryUseCase
  1. 校验 session 为 COLLECTING
  2. 在本地将状态改为 GENERATING
  3. 固化 source IDs 快照
  4. 调用 DiaryRepository.generateDraft
  5. 校验响应来源与字段
  6. 在事务中保存 draft 并改为 REVIEWING
  7. 失败则保存失败原因，session 回到可重试状态
```

生成请求必须带 `clientRequestId`。同一请求重试不能创建多个草稿。原始片刻永远先于网络请求落库。

## 9. 导出方案

第一版完全在 Android 本地完成：

- 使用专门的 Export Composable，而不是直接截图阅读页；
- 固定内容宽度和字体，按内容高度分页或生成长图；
- 生成前等待字体和图片加载完成；
- Android 10 及以上使用 MediaStore 保存；
- 对超长正文设置分页上限并提供多张导出；
- 为 1080px 和高分辨率输出分别做像素验收。

## 10. 测试策略

### 单元测试

- DiarySession 状态转换；
- UseCase 成功、失败、重试和幂等；
- DTO/Domain/Entity 映射；
- AI 输出校验；
- ViewModel 发送中与错误恢复。

### 数据测试

- Room DAO 查询和事务；
- SharedPreferences 到 Room 的迁移；
- schema migration；
- 删除、恢复和日期边界。

### UI 测试

- 开始日记并连续记录；
- 结束后进入生成和审阅；
- 编辑后装订；
- 网络失败后重试；
- 字体放大、深色模式和关键 TalkBack 描述。

### 发布门禁

每个功能提交至少通过：

```powershell
gradle.bat testDebugUnitTest assembleDebug lintDebug
```

里程碑结束时额外运行 Release 构建和核心 Compose UI 测试。

## 11. 安全与隐私

- 不使用 Android `allowBackup=true` 保存敏感日记的默认云备份，发布前明确备份策略；
- 不在异常、Analytics 或 Crash 日志中记录正文；
- 自定义角色输入必须限制长度并转义；
- 应用锁后续使用 BiometricPrompt，不自行保存明文 PIN；
- 敏感设置需要时使用 Android Keystore；
- AI 发送前在设置页解释发送范围和供应商处理边界。

## 12. 重构原则

- 不一次性重写全部 UI；
- 先建立新数据和领域边界，再逐个 feature 迁移；
- 每次迁移后旧入口必须删除，不能长期保留两套实现；
- 重构提交不混入新视觉设计；
- 任何迁移都必须能读取已有用户数据。
