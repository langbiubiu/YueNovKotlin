package com.yuenov.kotlin.open.model.request

// 提交产品问题
data class SubmitProductProblemRequest(
    var desc: String? = null,
    var contact: String? = null
)