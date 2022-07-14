package com.yuenov.kotlin.open.ext

import android.content.Context

//图书连载状态：完结 或 连载
const val CHAPTER_STATUS_END = "END"
const val CHAPTER_STATUS_SERIALIZE = "SERIALIZE"

fun readFromAssets(context: Context, fileName: String): String {
    try {
        //得到资源中的asset数据流
        val inputStream = context.resources.assets.open(fileName)
        val length = inputStream.available()
        val buffer = ByteArray(length)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return ""
}