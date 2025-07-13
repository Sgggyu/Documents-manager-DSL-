package com.example

import java.io.File
import java.nio.file.InvalidPathException

class DocsManager(val docspath : File)  {


    init {
        require( docspath.exists() && docspath.isDirectory){
            "目录不存在或者路径不指向目录"
        }

    }
    fun addFile(file_name: String) : Boolean {
       return File("$docspath/$file_name").createNewFile()
    }
    fun deleteFile(
        fileName: String? = null,
        condition: ((File) -> Boolean)? = null,
        recursive:Boolean = true
    ) {
        fileName?.let {
            val file = resolveFile(it) //验证路径安全性
            if (file.exists()) file.deleteRecursively().also {
                if (!it) println("Failed to delete: ${file.absolutePath}")
            }
        }
        condition?.let {
            when{
                recursive -> docspath.walk().filter(it).forEach {childfile -> childfile.deleteRecursively() }
                else ->     docspath.listFiles()?.filter(  it )?.forEach {childfile -> childfile.delete() }
            }
        }

    }
    private fun resolveFile(fileName: String): File {
        // 1. 规范化文件名（移除特殊字符）
        val normalized = fileName.replace("/", "")

        // 2. 防止路径穿越攻击
        if (normalized.contains(File.separatorChar))
            throw InvalidPathException("File names cannot contain path separators", fileName)

        // 3. 组合路径并验证
        return File(docspath, normalized).also {
            // 双重验证路径安全
            if (it.absolutePath != docspath.resolve(normalized).absolutePath)
                throw SecurityException("Path traversal detected")
        }
    }

    //用于语法简化的重载函数
    operator fun invoke(body: DocsManager.() -> Unit){
        body()
    }
}