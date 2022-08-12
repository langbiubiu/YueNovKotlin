package com.yuenov.kotlin.open.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.annotation.AnimRes
import androidx.core.view.isVisible
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.databinding.FragmentReadBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.utils.*
import com.yuenov.kotlin.open.viewmodel.ReadFragmentViewModel
import com.yuenov.kotlin.open.widget.mypage.*
import com.yuenov.kotlin.open.widget.page.IPagerLoader
import com.yuenov.kotlin.open.widget.page.animation.*
import me.hgj.jetpackmvvm.ext.nav

/**
 * 从外部调用DetailFragment时，先确保章节列表更新完毕，并且有确定的chapterId，
 * 当chapterId为0时，认为是没有阅读记录，从头第一章开始阅读
 */
class ReadFragment : BaseFragment<ReadFragmentViewModel, FragmentReadBinding>() {

    // 书籍基础信息
    private var bookBaseInfo: BookBaseInfo? = null
    // 当前章节ID
    private var chapterId: Long = 0
    // 章节列表，不包含章节内容
    private var menuList: ArrayList<TbBookChapter> = arrayListOf()
    // 当前章节，包含章节内容
    private var currentChapter: TbBookChapter? = null
    // 章节内页数
    private var pageNum: Int = 0

    //电池电量
    private var quantity: Int = 0

    private lateinit var readSettingInfo: ReadSettingInfo

//    private var pageLoader = ReadPageLoader()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                quantity = level
            } else if (intent?.action == Intent.ACTION_TIME_TICK) {
                // TODO 更新Time？
            }
        }
    }

    //---------------------- 重写方法以及初始化 ----------------------//
    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            // 点击事件回调
            setClickListener(
                rlDiTop,
                tvDiLeft,
                tvDiDownLoad,
                tvDiUpdateContent,
                tvDiAddShelf
            ) { view ->
                if (isLoadingShowing()) return@setClickListener
                when (view) {
                    rlDiTop -> if (rlDiTop.isVisible) hideOperation()
                    tvDiLeft -> {
                        hideOperationWithoutAnimation()
                        onBackPressed()
                    }
                    tvDiAddShelf -> mViewModel.addBookShelf(bookBaseInfo!!)
                    tvDiUpdateContent -> {
                        hideOperation()
                        mViewModel.updateChapterContent(bookBaseInfo!!.bookId, chapterId)
                    }
                    tvDiDownLoad -> toDownload()
                }
            }
            // 注册底部栏的各种事件回调
            dovDiOperation.setListener(object : PageOperationView.PageOperationViewListener {
                override fun <T> onDataChange(event: Int, newValue: T) {
                    when (event) {
                        PageOperationView.DATA_CHANGE_EVENT_BG_TYPE -> {
                            val bgType = newValue as PageBackground
                            if (readSettingInfo.pageBackground != bgType) {
                                readSettingInfo.pageBackground = bgType
//                                pvDiContent.invalidate()
                            }
                        }
                        PageOperationView.DATA_CHANGE_EVENT_LIGHT_VALUE -> {
                            val lightValue = newValue as Int
                            if (readSettingInfo.lightValue != lightValue) {
                                readSettingInfo.lightValue = lightValue
                                BrightnessUtils.setAppScreenBrightness(
                                    requireActivity(),
                                    lightValue
                                )
                            }
                        }
                        PageOperationView.DATA_CHANGE_EVENT_FONT_SIZE -> {
                            val textSize = newValue as Float
                            if (readSettingInfo.textSize != textSize) {
                                readSettingInfo.textSize = textSize
//                                pvDiContent.invalidate()
                            }
                        }
                        PageOperationView.DATA_CHANGE_EVENT_PAGE_ANIM_TYPE -> {
                            val animType = newValue as PageAnimationType
                            if (readSettingInfo.pageAnimType != animType) {
                                readSettingInfo.pageAnimType = animType
//                                pvDiContent.invalidate()
                            }
                        }
                    }
                    DataStoreUtils.putJsonData(
                        PreferenceConstants.KEY_READ_SETTING_INFO,
                        readSettingInfo
                    )
                }

                override fun onSelectChapter(chapterId: Long) {
                    if (chapterId < 1) return
                    hideOperation()
                    openChapter(chapterId, 0)
                }
            })
            // TODO 还有PageView的Touch Listener

            SystemBarUtils.hideStableStatusBar(requireActivity())
            val lp = rlDiTop.layoutParams as RelativeLayout.LayoutParams
            lp.setMargins(0, ScreenUtils.statusBarHeight, 0, 0)
            rlDiTop.layoutParams = lp
        }
    }

    override fun initData() {
        bookBaseInfo = arguments?.getParcelable(PreferenceConstants.EXTRA_MODEL_BOOK_BASE_INFO)
        chapterId = arguments?.getLong(PreferenceConstants.EXTRA_LONG_CHAPTER_ID, 0)!!
//        chapterId = 1317914605532459009L


        readSettingInfo = DataStoreUtils.getJsonData(
            PreferenceConstants.KEY_READ_SETTING_INFO,
            ReadSettingInfo::class.java,
            ReadSettingInfo()
        )

        if (bookBaseInfo == null || bookBaseInfo!!.bookId < 1) {
            showToast("未知数据")
            nav().navigateUp()
        }
        logd(CLASS_TAG, "bookInfo:$bookBaseInfo, chapterId:$chapterId")

        if (readSettingInfo.lightValue > 1) {
            BrightnessUtils.setAppScreenBrightness(
                requireActivity(),
                readSettingInfo.lightValue
            )
        }

        val lightValue =
            if (readSettingInfo.lightValue < 1)
                BrightnessUtils.getScreenBrightness(requireContext())
            else
                readSettingInfo.lightValue
        mViewBind.dovDiOperation.init(
            bookBaseInfo!!.title ?: "",
            menuList,
            chapterId,
            readSettingInfo.pageBackground,
            lightValue,
            readSettingInfo.textSize,
            readSettingInfo.pageAnimType
        )

        initPageLoader()

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    // 感觉没啥用
    private fun initPageLoader() {}

    override fun lazyLoadData() {
        mViewModel.apply {
            // 是否在书架中，用来更新上top栏的图标
            hasBookShelf(bookBaseInfo!!.bookId, false)
            // 获取章节列表
            getChapterList(bookBaseInfo!!.bookId)
            // 查询并更新章节列表
            updateChapterList(bookBaseInfo!!.bookId, false)
        }
    }

    override fun createObserver() {
        mViewModel.apply {
            hasBookShelfState.observe(viewLifecycleOwner) {
                resetVisibility(!it, mViewBind.tvDiAddShelf)
            }
            getChapterListState.observe(viewLifecycleOwner) {
                if (it.isSuccess && !it.isEmpty) {
                    logd(CLASS_TAG, "getChapterListState ${it.listData.size}")
                    menuList.clear()
                    menuList.addAll(it.listData)
                    mViewBind.dovDiOperation.setMenuList(menuList)
                    // 初始化起始阅读位置
                    getStartChapterAndPage(bookBaseInfo!!.bookId, chapterId)
                }
            }
            updateChapterListState.observe(viewLifecycleOwner) {
                if (it.isSuccess && it.data != null && !it.data!!.chapters.isNullOrEmpty()) {
                    for (chapter in it.data!!.chapters!!) {
                        // 把更新的章节添加到list
                        menuList.add(
                            TbBookChapter(
                                bookBaseInfo!!.bookId,
                                chapter.id,
                                chapter.name,
                                chapter.content,
                                chapter.v
                            )
                        )
                    }
                    mViewBind.dovDiOperation.setMenuList(menuList)
                }
                getStartChapterAndPage(bookBaseInfo!!.bookId, chapterId)
            }
            getStartChapterAndPageState.observe(viewLifecycleOwner) {
                logd(CLASS_TAG, "getStartChapterAndPage Pair:$it")
                it?.apply {
                    chapterId = first
                    pageNum = second
                }
                // 记录当前阅读的书籍，用于异常退出后下次打开APP时的恢复操作，BookShelfFragment的openLastBook()
                DataStoreUtils.putJsonData(PreferenceConstants.KEY_NOW_READING_BOOK_ID, bookBaseInfo)
                openChapter(chapterId, pageNum)
            }
            addBookShelfState.observe(viewLifecycleOwner) {
                if (it) {
                    resetVisibility(false, mViewBind.tvDiAddShelf)
                    showToast(R.string.info_addBookShelf_success)
                }
            }
            updateChapterContentState.observe(viewLifecycleOwner) {
                if (it) {
                    openChapter(chapterId, pageNum)
                    showToast("刷新成功")
                } else {
                    showToast("刷新失败")
                }
            }
            downloadChapterContentState.observe(viewLifecycleOwner) {
                if (it.isSuccess && !it.isEmpty) {
                    if (chapterId == it.listData[0].id) {
                        //更新的是当前章节，则重新绘制
                        openChapter(it.listData[0].id, pageNum)
                    }
                } else {
                    showToast("下载失败")
                    if (!it.isSuccess) {
                        // TODO 更新v值
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SystemBarUtils.hideStableStatusBar(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(receiver)
    }

    override fun needBackPressedCallback(): Boolean {
        return true
    }

    override fun onBackPressed() {
        logd(CLASS_TAG, "onBackPressed")
        if (mViewBind.rlDiTop.isVisible) {
            showOrHideOperation()
        } else {
            animation?.cancel()
            SystemBarUtils.showStableStatusBar(requireActivity())
            super.onBackPressed()

            if (isLoadingShowing()) dismissLoading()
            // 正常退出清掉最后阅读的图书
            DataStoreUtils.putData(PreferenceConstants.KEY_NOW_READING_BOOK_ID, "")
        }
    }

    //--------------------- 业务逻辑 ----------------------//
    // 打开指定章节
    private fun openChapter(chapterId: Long, pageNum: Int) {
        logd(CLASS_TAG, "openChapter chapterId:$chapterId pageNum:$pageNum")
        this.chapterId = chapterId
        this.pageNum = pageNum
        currentChapter = mViewModel.getChapter(bookBaseInfo!!.bookId, chapterId)
        if (currentChapter == null || currentChapter!!.content.isNullOrEmpty()) {
            //mViewBind.pvDiContent.drawCurPage(true)
            downloadChapter(chapterId)
        } else {
            initReadInfo()
            //mViewBind.pvDiContent.drawCurPage(false)
        }
    }

    private fun downloadChapter(chapterId: Long) {
        logd(CLASS_TAG, "downloadChapter chapterId:$chapterId")
        mViewModel.downloadChapterContent(bookBaseInfo!!.bookId, chapterId, menuList[0].v)

    }

    private fun initReadInfo() {
        logd(CLASS_TAG, "initReadInfo")
        currentChapter = mViewModel.getChapter(bookBaseInfo!!.bookId, chapterId)
        mViewModel.addReadHistory(bookBaseInfo!!, chapterId, pageNum)
        autoDownload()
    }

    /**
     * 自动下载前后章节
     */
    private fun autoDownload() {
        logd(CLASS_TAG, "autoDownload menu size:${menuList.size}")
        if (menuList.isEmpty()) return
        // 记录下载的两章在列表中的位置
        val posList = mutableListOf<Int>()
        val firstChapter: Long
        val secondChapter: Long
        val position = menuList.indexOfFirst { it.chapterId == chapterId }
        logd(CLASS_TAG, "autoDownload position:$position")
        if (position >= 0) {
            if (position - 1 >= 0) { // 取前一章
                posList.add(position-1)
            } else if (position + 2 <= menuList.size - 1) { // 取后下下一章
                posList.add(position+2)
            }
            if (position + 1 <= menuList.size - 1) { // 取后一章
                posList.add(position+1)
            } else if (position -2 >= 0) { // 取前前一章
                posList.add(position-2)
            }
        }
        logd(CLASS_TAG, "autoDownload posList:$posList")
        if (posList.size == 2) {
            val first = mViewModel.getChapter(bookBaseInfo!!.bookId, menuList[posList[0]].chapterId)
            first?.apply {
                if (content.isNullOrEmpty()) {
                    firstChapter = chapterId
                    downloadChapter(firstChapter)
                    logd(CLASS_TAG, "autoDownload first:$firstChapter")
                } else {
                    logd(CLASS_TAG, "first chapter has been downloaded!")
                }
            }
            val second = mViewModel.getChapter(bookBaseInfo!!.bookId, menuList[posList[1]].chapterId)
            second?.apply {
                if (content.isNullOrEmpty()) {
                    secondChapter = chapterId
                    downloadChapter(secondChapter)
                    logd(CLASS_TAG, "autoDownload second:$secondChapter")
                } else {
                    logd(CLASS_TAG, "second chapter has been downloaded!")
                }
            }
        } else if (posList.size == 1) {
            val first = mViewModel.getChapter(bookBaseInfo!!.bookId, menuList[posList[0]].chapterId)
            first?.apply {
                if (!content.isNullOrEmpty()) {
                    firstChapter = chapterId
                    downloadChapter(firstChapter)
                    logd(CLASS_TAG, "autoDownload first:$firstChapter")
                }
            }
        }
    }

    //--------------------- UI相关 ------------------------//
    private fun hideOperation() {
        mViewBind.apply {
            dovDiOperation.hideAllContent()
            setAnimation(R.anim.slide_top_out, rlDiTop, false)
            setAnimation(R.anim.slide_bottom_out, dovDiOperation, false)
        }
    }

    private fun hideOperationWithoutAnimation() {
        mViewBind.apply {
            resetVisibility(false, rlDiTop)
            resetVisibility(false, dovDiOperation)
        }
    }

    private fun showOrHideOperation() {
        mViewBind.apply {
            if (rlDiTop.isVisible) {
                if (dovDiOperation.isShowContent()) {
                    dovDiOperation.hideAllContent()
                } else {
                    hideOperation()
                }
            } else {
                dovDiOperation.setMenuList(menuList)
                setAnimation(R.anim.slide_top_in, rlDiTop, true)
                setAnimation(R.anim.slide_bottom_in, dovDiOperation, true)
            }
        }
    }

    private var animation: Animation? = null
    private fun setAnimation(@AnimRes animId: Int, view: View, isShow: Boolean) {
        animation?.cancel()
        animation = AnimationUtils.loadAnimation(context, animId)
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                if (isShow) {
                    SystemBarUtils.showUnStableStatusBar(requireActivity())
                }
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (!isShow) {
                    SystemBarUtils.hideStableStatusBar(requireActivity())
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        resetVisibility(isShow, view)
        view.startAnimation(animation)
    }

    inner class ReadPageLoader : IPagerLoader {
       private var curPageAnimType: PageAnimationType = PageAnimationType.NONE
//       private var curPageAnimation: PageAnimation = NonePageAnimation(mViewBind.pvDiContent)

       override fun getBattery(): Int = quantity

       override fun getContent(): String {
           return currentChapter?.content ?: ""
       }

       override fun getTime(): String = TimeUtils.getShowTimeText()

       override fun getTitle(): String {
           return currentChapter?.chapterName ?: ""
       }

        override fun getProgressPercent(): String {
            TODO("Not yet implemented")
        }

       override fun getBgColor(): Int = readSettingInfo.pageBackground.bgColor

       override fun getTextColor(): Int = readSettingInfo.pageBackground.textColor

       override fun getTextSize(): Float = readSettingInfo.textSize

       override fun getPageAnimation(): PageAnimation {
//           if (curPageAnimType == readSettingInfo.pageAnimType) return curPageAnimation
//           curPageAnimation = when(readSettingInfo.pageAnimType) {
//               PageAnimationType.SIMULATION -> SimulationPageAnimation(mViewBind.pvDiContent)
//               PageAnimationType.COVER -> CoverPageAnimation(mViewBind.pvDiContent)
//               PageAnimationType.SLIDE -> SlidePageAnimation(mViewBind.pvDiContent)
//               PageAnimationType.NONE -> NonePageAnimation(mViewBind.pvDiContent)
//               PageAnimationType.SCROLL -> VerticalPageAnimation(mViewBind.pvDiContent)
//           }
//           curPageAnimType = readSettingInfo.pageAnimType
//           return curPageAnimation
           TODO("Not yet implemented")
       }

       override fun allowTurnPage(isPrev: Boolean): Boolean {
           return if (mViewBind.rlDiTop.isVisible) {
               hideOperation()
               false
           } else {
               true
           }
       }

       override fun hasNextContent(isPrev: Boolean): Boolean {
           TODO("Not yet implemented")
       }

   }
}