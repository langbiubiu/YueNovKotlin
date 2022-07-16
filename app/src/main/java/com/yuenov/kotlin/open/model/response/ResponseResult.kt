package com.yuenov.kotlin.open.model.response

/**
 * HTTP返回的数据状态信息
 */
data class ResponseResult(
    val code: Int = 0,
    val msg: String = ""
) {
    companion object {
        /**
         * 返回数据正确
         */
        private const val SUCCESS = 0

        /**
         * 新用户创建成功
         */
        private const val USER_CREATE_SUCCESS = 101

        /**
         * 未查询到数据
         */
        private const val NO_DATA = 102

        /**
         * 书源已经失效
         */
        private const val INVALID_SOURCE = 203

        /**
         * 参数校验出错
         */
        private const val PARAM_ERROR = 1001

        /**
         * 返回值异常
         */
        private const val RETURN_ERROR = 1002

        /**
         * 非法请求
         */
        private const val ILLEGAL_REQUEST = 1003

        /**
         * 权限验证异常
         */
        private const val AUTH_ERROR = 1005

        /**
         * 远程调用服务超时
         */
        private const val REMOTE_SERVICE_TIMEOUT = 1007

        /**
         * 系统出错
         */
        private const val SYSTEM_ERROR = 9999
    }

    fun isSuccess(): Boolean = code == SUCCESS
}
