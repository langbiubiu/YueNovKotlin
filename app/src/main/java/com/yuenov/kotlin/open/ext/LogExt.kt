package com.yuenov.kotlin.open.ext

import android.text.TextUtils
import android.util.Log
import com.yuenov.kotlin.open.BuildConfig

const val DEFAULT_TAG = "LBB"

/**
 * 类名TAG，用于日志打印，不是静态变量所以只能用于类内部
 */
val Any?.CLASS_TAG: String
    get() {
        return this!!::class.java.simpleName
    }

private enum class LEVEL {
    V, D, I, W, E
}

fun logv(tag: String = DEFAULT_TAG, message: String) =
    log(LEVEL.V, tag, message)
fun logd(tag: String = DEFAULT_TAG, message: String) =
    log(LEVEL.D, tag, message)
fun logi(tag: String = DEFAULT_TAG, message: String) =
    log(LEVEL.I, tag, message)
fun logw(tag: String = DEFAULT_TAG, message: String) =
    log(LEVEL.W, tag, message)
fun loge(tag: String = DEFAULT_TAG, message: String) =
    log(LEVEL.E, tag, message)

/**
 * 这里使用自己分节的方式来输出足够长度的 message
 *
 * @param tag 标签
 * @param msg 日志内容
 */
fun logLongInfo(tag: String, msg: String) {
    var msg = msg
    if (!BuildConfig.DEBUG || TextUtils.isEmpty(msg)) {
        return
    }
    msg = msg.trim { it <= ' ' }
    var index = 0
    val maxLength = 3500
    var sub: String
    while (index < msg.length) {
        sub = if (msg.length <= index + maxLength) {
            msg.substring(index)
        } else {
            msg.substring(index, index + maxLength)
        }
        index += maxLength
        Log.d(tag, sub.trim { it <= ' ' })
    }
}

private fun log(level: LEVEL, tag: String, message: String) {
    if (!BuildConfig.DEBUG) return
    when (level) {
        LEVEL.V -> Log.v(tag, message)
        LEVEL.D -> Log.d(tag, message)
        LEVEL.I -> Log.i(tag, message)
        LEVEL.W -> Log.w(tag, message)
        LEVEL.E -> Log.e(tag, message)
    }
}