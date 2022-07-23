package com.yuenov.kotlin.open.utils

import java.util.*

object TimeUtils {

    fun getDiffTimeText(firstTime: Long):String {
        var diffTime = ""
        val currentTimes = Calendar.getInstance() //当前系统时间转Calendar类型
        val firstTimes = Calendar.getInstance()   //查询的数据时间转Calendar类型
        firstTimes.timeInMillis = firstTime

        val diff = currentTimes.timeInMillis - firstTimes.timeInMillis
        if (diff < 1) return diffTime

        //获取年
        var year = currentTimes.get(Calendar.YEAR) - firstTimes.get(Calendar.YEAR)
        var month = currentTimes.get(Calendar.MONTH) - firstTimes.get(Calendar.MONTH)
        val day = currentTimes.get(Calendar.DAY_OF_MONTH) - firstTimes.get(Calendar.DAY_OF_MONTH)
        if (day < 0) {
            month -= 1
            currentTimes.add(Calendar.MONTH, -1)
        }
        if (month < 0) {
            month = (month + 12) % 12; //获取月
            year--
        }
        val days = diff / (1000 * 60 * 60 * 24)
        //获取时
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        //获取分钟
        val minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        //获取秒
        val seconds = diff / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60

        if (year > 0) {
            diffTime = "${year}年前"
        } else if (month > 0) {
            diffTime = "${month}月前"
        } else if (days > 0) {
            diffTime = "${days}天前"
        } else if (hours > 0) {
            diffTime = "${hours}小时前"
        } else if (minutes > 0) {
            diffTime = "${minutes}分钟前"
        } else if (seconds > 0) {
            diffTime = "${seconds}秒前"
        }
        return diffTime
    }
}