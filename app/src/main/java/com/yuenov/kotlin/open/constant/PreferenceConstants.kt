package com.yuenov.kotlin.open.constant

object PreferenceConstants {

    //Activity和Fragment之间传递EXTRA时使用的Key
    const val EXTRA_INT_BOOK_ID = "BookId"
    const val EXTRA_MODEL_BOOK_BASE_INFO = "BookBaseInfo"
    const val EXTRA_STRING_TITLE = "Title"
    const val EXTRA_LONG_CHAPTER_ID = "ChapterId"
    const val EXTRA_STRING_CATEGORY_NAME = "CategoryName"
    const val EXTRA_INT_CATEGORY_ID = "CategoryId"
    const val EXTRA_INT_CHANNEL_ID = "ChannelId"
    const val EXTRA_STRING_TYPE = "type"

    //设置信息
    const val KEY_READ_SETTING_INFO = "ReadSettingInfo"
    const val KEY_CATEGORY_INFO = "CategoryInfo"
    const val KEY_SEARCH_HISTORY = "SearchHistory"

    //阅读偏好
    const val KEY_READING_PREFERENCES = "ReadingPreferences"

    //代理IP
    const val KEY_PROXY_IP = "proxyIp"
    const val KEY_UUID = "uuid"
    const val KEY_UID = "uid"

    //接口端口
    const val KEY_INTERFACE_PORT = "InterfacePort"

    //正在阅读的图书
    const val KEY_NOW_READING_BOOK_ID = "nowRead"

    /**
     * 发现页缓存
     */
    const val TYPE_FIND_INDEX = "findIndex"

    /**
     * 书城页缓存
     */
    const val TYPE_BOOKSTORE_START = "bookStore_"

    /**
     * 分类首页缓存
     */
    const val TYPE_CATEGORY_CHANNEL = "categoryChannel"
}