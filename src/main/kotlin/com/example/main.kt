package com.example
import java.io.File

fun main() {
    println("===== 开始测试 DocsManager =====")

    // 1. 创建测试目录
    val testDir = File("test_docs").apply {
        if (exists()) deleteRecursively()
        mkdirs()
        println("创建测试目录: ${absolutePath}")
    }

    // 2. 初始化 DocsManager
    val manager = try {
        DocsManager(testDir).also {
            println("DocsManager 初始化成功")
        }
    } catch (e: Exception) {
        println("初始化失败: ${e.message}")
        return
    }

    // 3. 测试添加文件
    println("\n=== 测试添加文件 ===")
    val fileName = "test_file.txt"
    val addResult = manager.addFile(fileName)
    println("添加文件 '$fileName': ${if (addResult) "成功" else "失败"}")

    // 验证文件是否存在
    val testFile = File(testDir, fileName)
    println("文件存在: ${testFile.exists()}")

    // 4. 测试按文件名删除
    println("\n=== 测试按文件名删除 ===")
    manager.deleteFile(fileName = fileName)
    println("文件存在: ${testFile.exists()}")

    // 5. 测试按条件删除（递归）
    println("\n=== 测试按条件删除（递归） ===")
    // 创建测试文件结构
    File(testDir, "file1.txt").createNewFile()
    File(testDir, "file2.log").createNewFile()
    val subDir = File(testDir, "subdir").apply { mkdirs() }
    File(subDir, "file3.txt").createNewFile()
    File(subDir, "file4.log").createNewFile()

    println("删除前文件列表:")
    testDir.walk().forEach { if (it.isFile) println("- ${it.absolutePath}") }

    // 删除所有 .log 文件（递归）
    manager.deleteFile(condition = { it.extension == "log" }, recursive = true)

    println("\n删除后文件列表:")
    testDir.walk().forEach { if (it.isFile) println("- ${it.absolutePath}") }

    // 6. 测试按条件删除（非递归）
    println("\n=== 测试按条件删除（非递归） ===")
    // 创建更多文件
    File(testDir, "file5.txt").createNewFile()
    File(testDir, "file6.log").createNewFile()

    println("删除前文件列表:")
    testDir.walk().forEach { if (it.isFile) println("- ${it.absolutePath}") }

    // 删除顶层目录的所有文件（非递归）
    manager.deleteFile(condition = { it.isFile }, recursive = false)

    println("\n删除后文件列表:")
    testDir.walk().forEach { if (it.isFile) println("- ${it.absolutePath}") }

    // 7. 测试 DSL 语法
    println("\n=== 测试 DSL 语法 ===")
    manager {
        addFile("dsl_test.txt")
        println("DSL 添加文件后存在: ${File(testDir, "dsl_test.txt").exists()}")

        deleteFile(fileName = "dsl_test.txt")
        println("DSL 删除文件后存在: ${File(testDir, "dsl_test.txt").exists()}")
    }

    // 8. 测试安全路径解析
    println("\n=== 测试安全路径解析 ===")
    try {
        manager.addFile("../malicious.txt")
        println("路径穿越攻击测试失败 - 文件被创建")
    } catch (e: Exception) {
        println("路径穿越攻击测试成功: ${e.message}")
    }

    // 9. 清理测试目录
    testDir.deleteRecursively()
    println("\n测试完成，清理目录")
}