package com.yuenov.kotlin.open.ext

import android.content.Context

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