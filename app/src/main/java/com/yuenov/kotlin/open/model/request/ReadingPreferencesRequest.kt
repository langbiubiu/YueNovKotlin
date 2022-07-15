package com.yuenov.kotlin.open.model.request

/**
 * 用户性别更新请求body，需要转为json，见[ApiService.userUpdate()]
 */
data class ReadingPreferencesRequest (
    var gender: String? = null
)