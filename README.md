# Momentary | 片刻

一个用最低操作成本接住想法和情绪，再把当天碎片整理成日记的 Android 应用。

## 当前可用功能

- 今日片刻：快速输入、心情选择、时间线和收藏。
- 多角色陪伴：三种内置角色、自定义角色、角色独立对话和 AI 网关回复。
- 和片刻聊聊：切换陪伴角色、收藏回复、收录到今日。
- 今日总结：事件、状态、灵感、感谢、待办和温和回应。
- 日记本：保存今日总结、查看历史日记和按月浏览。
- 我的回响：筛选收藏的 AI 回复与个人笔记。
- 本地优先：片刻、心情、聊天、收藏、日记和角色均保存在设备上。
- 浅色和深色主题令牌、TalkBack 描述、48dp 最小点击区域。

## 技术栈

- Kotlin 2.3.21
- Jetpack Compose + Material 3
- Navigation Compose
- StateFlow + AndroidViewModel
- Android Gradle Plugin 8.13.2

## 运行

使用 Android Studio 打开项目，等待 Gradle Sync 后运行 `app` 配置。命令行构建：

```powershell
.\gradlew.bat assembleDebug
```

Debug APK 输出到：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## AI 接入边界

Android 客户端只连接受控 AI 网关，不保存模型供应商的 API Key。普通聊天最多发送当前角色最近 20 条对话；日记采集模式不会请求 AI，只在本地保存内容并回复“收到”。

Debug 构建默认访问 Android 模拟器宿主机的 `http://10.0.2.2:8787`。可在构建时覆盖网关地址：

```powershell
.\gradlew.bat assembleDebug -PAI_GATEWAY_URL=https://your-gateway.example.com
```

Release 构建没有默认地址，必须通过 `AI_GATEWAY_URL` 注入 HTTPS 网关。API Key 只能配置在网关运行环境中，不能写入 `local.properties`、Gradle 脚本或 APK。

## 开发计划

当前阶段范围、验收标准与后续路线见 [`docs/下一阶段开发计划.md`](docs/下一阶段开发计划.md)。
