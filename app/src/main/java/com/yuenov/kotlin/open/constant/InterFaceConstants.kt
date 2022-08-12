package com.yuenov.kotlin.open.constant

import com.yuenov.kotlin.open.application.appViewMode

object InterFaceConstants {

    /**
     * 默认分页条数
     */
    const val pageSize: Int = 20

    /**
     * 书城列表
     */
    const val categoriesListPageSize = pageSize

    //图书连载状态：完结 或 连载
    const val CHAPTER_STATUS_END = "END"
    const val CHAPTER_STATUS_SERIALIZE = "SERIALIZE"

    //分类类型
    const val CATEGORY_TYPE_READ_MOST = "READ_MOST"  //大家都在看
    const val CATEGORY_TYPE_RECENT_UPDATE = "RECENT_UPDATE" //最近更新
    const val CATEGORY_TYPE_CATEGORY = "CATEGORY" //普通分类

    /**
     * 默认端口
     */
    private const val domainDefaultPort = 80

    /**
     * 端口
     */
    var domainPort: Int = domainDefaultPort
        get() {
            return if (field == domainDefaultPort
                && !appViewMode.appConfigInfo.value?.ports.isNullOrEmpty()
            ) {
                appViewMode.appConfigInfo.value!!.ports!![0]
            } else {
                field
            }
        }

    /**
     * 接口域名
     */
    private const val INTERFACE_DOMAIN = "http://yuenov.com"

    /**
     * 图片域名
     */
    private const val IMAGE_DOMAIN = "http://pt.yuenov.com"

    /**
     * 获取接口完整路径
     */
    fun getInterfaceDomain(): String = "$INTERFACE_DOMAIN:$domainPort/app/"

    /**
     * 获取主页地址
     */
    fun getUrlDomain(): String = "$INTERFACE_DOMAIN:$domainPort"

    /**
     * 获取图片接口路径
     */
    fun getImageDomain(): String = "$IMAGE_DOMAIN:$domainPort"

}