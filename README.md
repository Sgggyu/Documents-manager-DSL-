# Documents Manager DSL 使用指南

## 功能特性
- **初始化文档管理器**：创建并验证目标目录
- **添加文件**：支持单个或批量添加文件
- **删除文件**：支持按文件名或条件删除，可递归操作
- **DSL 风格操作**：使用简洁的链式调用语法
- **安全防护**：内置路径安全验证机制

## 快速开始

### 1. 初始化 DocsManager
```kotlin
val docsDir = File("/path/to/your/directory")
val manager = DocsManager(docsDir)
```

**初始化要求**：
- 目录必须存在
- 路径必须指向一个目录（不能是文件）
- 如果目录不存在或无效，会抛出 `IllegalArgumentException`

### 2. 添加文件
```kotlin
// 添加单个文件
val success = manager.addFile("report.txt")
```
```kotlin
// 使用 DSL 风格批量添加文件
manager {
    for (i in 1 until 10) {
        addFile("notes$i.md")  // 创建 notes1.md 到 notes9.md
    }
}
```

**注意事项**：
- 如果文件已存在，不会覆盖（返回 `false`）
- 文件名不能包含路径分隔符（如 `/` 或 `\`），防止路径攻击

### 3. 删除文件
**按文件名删除**：
```kotlin
// 删除单个文件
manager.deleteFile(fileName = "temp.txt")

// 删除目录及其所有内容
manager.deleteFile(fileName = "old_data", recursive = true)
```

**按条件删除**：
```kotlin
// 删除所有 .tmp 文件（递归操作）
manager.deleteFile(condition = { it.extension == "tmp" })

// 删除大于 100MB 的文件（仅当前目录）
manager.deleteFile(
    condition = { it.length() > 100  1024  1024 },
    recursive = false
)
```

**组合删除**：
```kotlin
// 先删除特定文件，再删除匹配条件的文件
manager.deleteFile(
    fileName = "obsolete.log",
    condition = { it.name.startsWith("cache_") }
)
```

### 4. DSL 风格操作
使用简洁的 DSL 语法进行链式操作：

```kotlin
manager {
    // 添加配置文件
    addFile("config.yml")
    addFile("settings.json")
    
    // 删除操作
    deleteFile(fileName = "backup.zip")
    
    // 删除30天前的文件
    deleteFile(condition = { 
        it.lastModified() < System.currentTimeMillis() - 30  24  3600 * 1000 
    })
}
```

### 5. 安全特性
- **路径安全防护**：
  - 自动过滤文件名中的特殊字符
  - 双重路径验证机制
  - 防止路径穿越攻击

## 注意事项
1. 删除操作不可逆，请谨慎使用
2. 递归删除目录时会删除所有子内容
3. 文件名中不要使用特殊字符
4. 操作前确保有足够权限
5. 处理大文件时注意性能影响

##后续更新
可能会添加一个select返回符合参数条件的filelist
