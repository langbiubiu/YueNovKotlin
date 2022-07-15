package com.yuenov.kotlin.open.constant

import com.yuenov.kotlin.open.utils.AppConfigUtils

object InterFaceConstants {

    /**
     * 默认分页条数
     */
    val pageSize: Int = 20

    /**
     * 书城列表
     */
    val categoriesListPageSize = pageSize

    /**
     * 默认端口
     */
    private const val domainDefaultPort = 80

    /**
     * 端口
     */
//    var domainPort: Int = domainDefaultPort
//        get() {
//            return if (field == domainDefaultPort
//                && !AppConfigUtils.getAppConfigInfo()?.ports.isNullOrEmpty()
//            ) {
//                AppConfigUtils.getAppConfigInfo()!!.ports!![0]
//            } else {
//                domainDefaultPort
//            }
//        }
    //暂时用这个端口来测试接口
    var domainPort = 19999

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
    fun getInterfaceDomain(): String = "$INTERFACE_DOMAIN:$domainPort//app/"

    /**
     * 获取主页地址
     */
    fun getUrlDomain(): String = "$INTERFACE_DOMAIN:$domainPort"

    /**
     * 获取图片接口路径
     */
    fun getImageDomain(): String = "$IMAGE_DOMAIN:$domainPort"

}