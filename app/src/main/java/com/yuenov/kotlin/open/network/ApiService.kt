package com.yuenov.kotlin.open.network

import com.yuenov.kotlin.open.constant.InterFaceConstants
import com.yuenov.kotlin.open.model.response.ApiResponse
import com.yuenov.kotlin.open.model.response.BookCheckUpdateResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    companion object {
        val API_URL: String = InterFaceConstants.getInterfaceDomain() + "open/api/"
    }

    /**
     * 批量检查书籍是否有更新
     */
    @Headers("Content-Type: application/json")
    @POST("book/checkUpdate")
    fun checkUpdate(@Body body: String): ApiResponse<BookCheckUpdateResponse>

    /**
     * 更新用户性别，文档中没有记录该接口，需要测试，确认返回结果的数据结构
     * @param body 具体内容见[ReadingPreferencesRequest]
     */
    @Headers("Content-Type: application/json")
    @POST("user/update")
    fun userUpdate(@Body body: String): ApiResponse<String>

    /**
     * 更新用户性别，文档中没有记录该接口，需要测试，确认返回结果的数据结构
     * @param body 具体内容见[SubmitSaveProductProblemRequest]
     */
    @Headers("Content-Type: application/json")
    @POST("problem/saveProductProblem")
    fun saveProductProblem(@Body body: String): ApiResponse<String>

}