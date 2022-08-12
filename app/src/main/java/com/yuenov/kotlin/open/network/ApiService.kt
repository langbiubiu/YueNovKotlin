package com.yuenov.kotlin.open.network

import com.yuenov.kotlin.open.constant.InterFaceConstants
import com.yuenov.kotlin.open.model.request.*
import com.yuenov.kotlin.open.model.response.*
import retrofit2.http.*

interface ApiService {

    companion object {
        val API_URL: String = InterFaceConstants.getInterfaceDomain() + "open/api/"
    }

    /**
     * 批量检查书籍是否有更新
     * @param body 必需 具体内容见[BookCheckUpdateRequest]
     */
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("book/checkUpdate")
    suspend fun checkUpdate(@Body body: BookCheckUpdateRequest): ApiResponse<CheckUpdateResponse>

    /**
     * 根据关键词搜索书籍
     * @param keyWord 必需 书籍关键词
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     */
    @GET("book/search")
    suspend fun searchBook(
        @Query("keyWord") keyWord: String,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<BookListResponse>

    /**
     * App内发现页面接口
     */
    @GET("category/discovery")
    suspend fun categoryDiscovery(): ApiResponse<FindIndexInfoResponse>

    /**
     * 书籍的全部分类
     */
    @GET("category/getCategoryChannel")
    suspend fun getCategoryChannel(): ApiResponse<CategoryChannelListResponse>

    /**
     * 书籍榜单信息
     */
    @GET("rank/getList")
    suspend fun getRankList(): ApiResponse<RankListResponse>

    /**
     * 每个榜单内的书籍列表
     * @param channelId 必需 频道号
     * @param rankId 必需 榜单号
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     */
    @GET("rank/getPage")
    suspend fun getRankPage(
        @Query("channelId") channelId: Int,
        @Query("rankId") rankId: Int,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<BookListResponse>

    /**
     * 所有完本书籍信息
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     */
    @GET("category/getCategoryEnd")
    suspend fun getCategoryEnd(
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<CategoryEndListResponse>

    /**
     * 书籍专题信息
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     */
    @GET("book/getSpecialList")
    suspend fun getSpecialList(
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<SpecialListResponse>

    /**
     * 专题下全部的书籍和换一换列表
     * @param id 必需 专题号
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     */
    @GET("book/getSpecialPage")
    suspend fun getSpecialPage(
        @Query("id") id: Int,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<BookListResponse>

    /**
     * 查看发现页某个分类下的全部或部分内容（查看全部，换一换）
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     * @param type 必需 发现页的分类类型
     * READ_MOST：大家都在看
     * RECENT_UPDATE: 最近更新
     * CATEGORY: 书籍分类
     * @param categoryId 非必需 书籍分类号，仅当type=CATEGORY有效
     */
    @GET("category/discoveryAll")
    suspend fun categoryDiscoveryAll(
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
        @Query("type") type: String,
        @Query("categoryId") categoryId: Int? = null
    ): ApiResponse<BookListResponse>

    /**
     * 某个分类下所有的书籍
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     * @param categoryId 必需 书籍所属的分类号
     * @param channelId 必需 某个频道下的分类书籍
     * @param orderBy 必需 分类书籍排序与筛选规则，不传返回全部书籍默认排序
     * NEWEST : 按照最新的书籍进行排序
     * HOT : 按照最火爆的书籍进行排序
     * END : 筛选已完结的书籍
     */
    @GET("book/getCategoryId")
    suspend fun getCategoryId(
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
        @Query("categoryId") categoryId: Int,
        @Query("channelId") channelId: Int,
        @Query("orderBy") orderBy: String
    ): ApiResponse<BookListResponse>

    /**
     * 每本书的详细信息
     * @param bookId 必需 书籍号
     */
    @GET("book/getDetail")
    suspend fun getDetail(
        @Query("bookId") bookId: Int
    ): ApiResponse<BookDetailInfoResponse>

    /**
     * 在书籍详情中的推荐列表和换一换
     * @param bookId 必需 书籍号
     * @param pageNum 必需 请求第几页的数据，pageNum最小值为1
     * @param pageSize 必需 请求每页多少条的数据
     */
    @GET("book/getRecommend")
    suspend fun getRecommend(
        @Query("bookId") bookId: Int,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<BookListResponse>

    /**
     * 获取书籍全部或部分目录信息，当书籍有更新时需要调用该接口来更新本地保存的目录信息，传入chapterId获取此章节
     * 之后的数据，不传chapterId是获取全部目录。如果本地已经保存过目录信息，建议最好传入最后一章chapterId来更新
     * 本地目录。而不是全量获取所有的目录。
     * @param bookId 必需 书籍号
     * @param chapterId 非必需 从第几章开始请求目录信息，如果不传请求全部的目录信息
     * @param v 非必需，
     */
    @GET("chapter/getByBookId")
    suspend fun getChapterByBookId(
        @Query("bookId") bookId: Int,
        @Query("chapterId") chapterId: Long,
        @Query("v") v: Int?
    ): ApiResponse<ChapterListResponse>

    /**
     * 下载章节内容，目前开放接口不支持批量下载，即每次请求只能传一个chapterId。
     * 由于书源失效会导致部分书籍不可访问。如果错误码为203表示书源已经失效，此时服务器会自动更新书源。
     * 如果书源失效并返回203请重新调用书籍目录接口[getChapterByBookId]来获取最新的目录信息
     * 此时当前接口需要传递v这个参数，这个参数由[getChapterByBookId]接口返回。
     * v表示当前此书籍的版本。默认为0，表示此书籍的书源没有失效过。1表示为此书籍的书源更新过一次。
     * [getChapterByBookId]返回v=1,如果当前接口传v=0表示从旧书源获取内容，会导致获取内容失败。
     *
     * @param body 详见[DownloadChapterRequest]
     */
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("chapter/get")
    suspend fun downloadChapter(@Body body: DownloadChapterRequest): ApiResponse<DownloadChapterListResponse>

    /**
     * 刷新章节内容，获取最新的章节数据，不支持批量下载，即每次请求只能传一个chapterId。
     * 与下载不一样，下载是获取服务器缓存的数据，但不是最新的数据。一般是下载的内容不正确时会调用该接口。
     * 该接口响应时间比较长，谨慎调用。
     * @param body 详见[ChapterUpdateRequest]
     */
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("chapter/updateForce")
    suspend fun updateChapter(@Body body: ChapterUpdateRequest): ApiResponse<DownloadChapterListResponse>

    /**
     * 获取热门搜索，书籍默认分类等配置信息，通常是在每次开机时启动
     */
    @GET("system/getAppConfig")
    suspend fun getAppConfig(): ApiResponse<AppConfigInfo>

    /**
     * 更新用户性别，文档中没有记录该接口，需要测试，确认返回结果的数据结构
     * @param body 具体内容见[ReadingPreferencesRequest]
     */
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("user/update")
    @Deprecated("接口可能已过期")
    suspend fun userUpdate(@Body body: ReadingPreferencesRequest): ApiResponse<String>

    /**
     * 更新用户性别，改变用户偏好，文档中没有记录该接口，需要测试，确认返回结果的数据结构
     * @param body 具体内容见[SubmitProductProblemRequest]
     */
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("problem/saveProductProblem")
    @Deprecated("接口可能已过期")
    suspend fun saveProductProblem(@Body body: SubmitProductProblemRequest): ApiResponse<String>

}