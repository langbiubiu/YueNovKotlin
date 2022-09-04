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
import com.yuenov.kotlin.open.widget.page.PageView
import com.yuenov.kotlin.open.widget.page.animation.*
import me.hgj.jetpackmvvm.ext.nav
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 从外部调用DetailFragment时，先确保章节列表更新完毕，并且有确定的chapterId，
 * 当chapterId为0时，认为是没有阅读记录，从头第一章开始阅读
 */
class ReadFragment : BaseFragment<ReadFragmentViewModel, FragmentReadBinding>() {

    // 书籍基础信息
    private var bookBaseInfo: BookBaseInfo? = null

    // 当前章节ID
    private var chapterId: Long = 0

    // 准备打开的章节ID
    private var openChapterId: Long = 0

    // 章节列表，不包含章节内容
    private var menuList: ArrayList<TbBookChapter> = arrayListOf()

    // 当前章节，包含章节内容
    private var currentChapter: TbBookChapter? = null

    // 下一章节，包含章节内容
    private var nextChapter: TbBookChapter? = null

    // 章节下载完后是否需要翻页
    // true表示向后翻页，false表示向前翻页
    private var isAutoTurnNext: Boolean = true

    // 章节内的页数
    private var pageNum: Int = 0

    //电池电量
    private var quantity: Int = 0

    private lateinit var readSettingInfo: ReadSettingInfo

    private lateinit var pageLoader: ReadPageLoader

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                quantity = level
                mViewBind.pvDiContent.drawCurPage(true)
            } else if (intent?.action == Intent.ACTION_TIME_TICK) {
                mViewBind.pvDiContent.drawCurPage(true)
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
                                pvDiContent.drawCurPage(true)
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
                                pvDiContent.updateContent()
                                pvDiContent.drawCurPage(true)
                            }
                        }
                        PageOperationView.DATA_CHANGE_EVENT_PAGE_ANIM_TYPE -> {
                            val animType = newValue as PageAnimationType
                            if (readSettingInfo.pageAnimType != animType) {
                                readSettingInfo.pageAnimType = animType
                                pvDiContent.drawCurPage(true)
                            }
                        }
                    }
                    DataStoreUtils.putJsonData(
                        PreferenceConstants.KEY_READ_SETTING_INFO,
                        readSettingInfo
                    )
                }

                override fun onSelectChapter(chapterId: Long) {
                    logD(CLASS_TAG, "onSelectChapter chapterId:$chapterId")
                    if (chapterId < 1) return
                    hideOperation()
                    openChapter(chapterId, 0)
                }
            })
            pvDiContent.touchListener = object : PageView.TouchListener {
                override fun onTouchUp() {
                    if (rlDiTop.isVisible) hideOperation()
                }

                override fun onCenter() {
                    showOrHideOperation()
                }
            }
            pvDiContent.listener = object : PageView.PageViewListener {
                override fun onTurnPageStart() {}

                override fun onTurnPageCompleted() {
                    pageNum = pvDiContent.curPageNum
                    mViewModel.addReadHistory(bookBaseInfo!!, chapterId, pageNum)
                }

                override fun onTurnPageCanceled() {}

                override fun onTurnChapterCompleted() {
                    logD(CLASS_TAG, "onTurnChapterCompleted")
                    currentChapter = nextChapter
                    chapterId = currentChapter!!.chapterId
                    mViewBind.dovDiOperation.setChapterId(chapterId)
                    nextChapter = null
                    mViewModel.addReadHistory(bookBaseInfo!!, chapterId, pageNum)
                    autoDownload()
                }
            }

            SystemBarUtils.hideStableStatusBar(requireActivity())
            val lp = rlDiTop.layoutParams as RelativeLayout.LayoutParams
            lp.setMargins(0, ScreenUtils.statusBarHeight, 0, 0)
            rlDiTop.layoutParams = lp
        }
    }

    override fun initData() {
        bookBaseInfo = requireArguments().getParcelable(PreferenceConstants.EXTRA_MODEL_BOOK_BASE_INFO)
        chapterId = requireArguments().getLong(PreferenceConstants.EXTRA_LONG_CHAPTER_ID, 0)

        readSettingInfo = DataStoreUtils.getJsonData(
            PreferenceConstants.KEY_READ_SETTING_INFO,
            ReadSettingInfo::class.java,
            ReadSettingInfo()
        )
        logD(CLASS_TAG, "initData $readSettingInfo")

        if (bookBaseInfo == null || bookBaseInfo!!.bookId < 1) {
            showToast("未知数据")
            nav().navigateUp()
        }
        logD(CLASS_TAG, "bookInfo:$bookBaseInfo, chapterId:$chapterId")

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
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun lazyLoadData() {
        pageLoader = ReadPageLoader()
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
                    logD(CLASS_TAG, "getChapterListState ${it.listData.size}")
                    menuList.clear()
                    menuList.addAll(it.listData)
                    mViewBind.dovDiOperation.setMenuList(menuList)
                    // 初始化起始阅读位置
                    getStartChapterAndPage(bookBaseInfo!!.bookId, chapterId)
                }
            }
            updateChapterListState.observe(viewLifecycleOwner) {
                if (it.isSuccess && it.data != null && !it.data!!.chapters.isNullOrEmpty()) {
                    val needStart = menuList.isEmpty()
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
                    if (needStart) getStartChapterAndPage(bookBaseInfo!!.bookId, chapterId)
                    mViewBind.dovDiOperation.setMenuList(menuList)
                }
            }
            getStartChapterAndPageState.observe(viewLifecycleOwner) {
                logD(CLASS_TAG, "getStartChapterAndPage Pair:$it")
                it?.apply {
                    chapterId = first
                    pageNum = second
                }
                // 记录当前阅读的书籍，用于异常退出后下次打开APP时的恢复操作，BookShelfFragment的openLastBook()
                DataStoreUtils.putJsonData(
                    PreferenceConstants.KEY_NOW_READING_BOOK_ID,
                    bookBaseInfo
                )
                openChapter(chapterId, pageNum)
                mViewBind.dovDiOperation.setChapterId(chapterId)
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
                    val downloadChapter = it.listData[0]
                    if (openChapterId == downloadChapter.id) {
                        //更新的是当前章节，则重新绘制
                        openChapter(downloadChapter.id, 0)
                        openChapterId = 0L
                    } else if (downloadChapter.id == nextChapter?.chapterId) {
                        mViewBind.pvDiContent.autoTurnPage(isAutoTurnNext)
                    }
                    menuList.find { chapter -> chapter.chapterId == downloadChapter.id }?.content = "0"
                } else {
                    showToast("章节下载失败")
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
        logD(CLASS_TAG, "onBackPressed")
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
        logD(CLASS_TAG, "openChapter chapterId:$chapterId pageNum:$pageNum")
        openChapterId = chapterId
        val chapter = mViewModel.getChapter(bookBaseInfo!!.bookId, chapterId)
        if (chapter == null || chapter.content.isNullOrEmpty()) {
            downloadChapter(chapterId, true)
        } else {
            this.chapterId = chapterId
            this.pageNum = pageNum
            currentChapter = chapter
            mViewModel.addReadHistory(bookBaseInfo!!, chapterId, pageNum)
            mViewBind.apply {
                dovDiOperation.setChapterId(chapterId)
                pvDiContent.curPageNum = pageNum
                pvDiContent.setPageLoader(pageLoader)
                pvDiContent.drawCurPage(false)
            }
            autoDownload()
        }
    }

    private fun downloadChapter(chapterId: Long, isShowLoading: Boolean) {
        logD(CLASS_TAG, "downloadChapter chapterId:$chapterId")
        mViewModel.downloadChapterContent(bookBaseInfo!!.bookId, chapterId, menuList[0].v, isShowLoading)
        // 网络请求存在12秒间隔，因此可能不会立刻显示loading框，手动调用一次，在请求完成后会调用dismiss
        if (isShowLoading) showLoading()
    }

    /**
     * 自动下载前后章节
     */
    private fun autoDownload() {
        if (menuList.isEmpty()) return
        // 记录下载的两章在列表中的位置
        val posList = mutableListOf<Int>()
        val position = menuList.indexOfFirst { it.chapterId == chapterId }
        if (position >= 0) {
            if (position - 1 >= 0) { // 取前一章
                posList.add(position - 1)
            } else if (position + 2 <= menuList.size - 1) { // 取后下下一章
                posList.add(position + 2)
            }
            if (position + 1 <= menuList.size - 1) { // 取后一章
                posList.add(position + 1)
            } else if (position - 2 >= 0) { // 取前前一章
                posList.add(position - 2)
            }
        }
        for (pos in posList) {
            val chapter = mViewModel.getChapter(bookBaseInfo!!.bookId, menuList[pos].chapterId)
            chapter?.apply {
                if (content.isNullOrEmpty()) {
                    downloadChapter(chapterId, false)
                    logD(CLASS_TAG, "autoDownload chapter:$chapterId $chapterName")
                } else {
                    logD(CLASS_TAG, "chapter[$chapterId $chapterName] has been downloaded!")
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
        private var curPageAnimation: PageAnimation = NonePageAnimation()

        override fun getBattery(): Int = quantity

        override fun getContent(): String = currentChapter?.content ?: ""

        override fun getNextContent(isNext: Boolean): String = nextChapter?.content ?: ""

        override fun getTime(): String = TimeUtils.getShowTimeText()

        override fun getTitle(): String = currentChapter?.chapterName ?: ""

        override fun getNextTitle(isNext: Boolean): String = nextChapter?.chapterName ?: ""

        override fun getProgress(pageNum: Int, pageCount: Int, isNextChapter: Boolean): String {
            val id = if (isNextChapter) nextChapter!!.chapterId else chapterId
            val position = menuList.indexOfFirst { it.chapterId == id }
            val format = DecimalFormat("0.00")
            format.roundingMode = RoundingMode.FLOOR
            val progress =
                if (menuList.isEmpty() || pageCount == 0) 0f
                else (position * pageCount + pageNum + 1) * 100f / (menuList.size * pageCount)
            return format.format(progress) + "%"
        }

        override fun getBgColor(): Int = readSettingInfo.pageBackground.bgColor

        override fun getTextColor(): Int = readSettingInfo.pageBackground.textColor

        override fun getTextSize(): Float = readSettingInfo.textSize

        override fun getPageAnimation(): PageAnimation {
            if (curPageAnimType == readSettingInfo.pageAnimType) return curPageAnimation
            curPageAnimation = when (readSettingInfo.pageAnimType) {
                PageAnimationType.SIMULATION -> SimulationPageAnimation()
                PageAnimationType.COVER -> CoverPageAnimation()
                PageAnimationType.SLIDE -> SlidePageAnimation()
                PageAnimationType.NONE -> NonePageAnimation()
                PageAnimationType.SCROLL -> VerticalPageAnimation()
            }
            curPageAnimType = readSettingInfo.pageAnimType
            return curPageAnimation
        }

        override fun canTouch(): Boolean = !isLoadingShowing()

        override fun allowPageAnimation(): Boolean = !mViewBind.rlDiTop.isVisible

        override fun hasNextChapter(isNext: Boolean): Boolean {
            val curIndex = menuList.indexOfFirst { it.chapterId == currentChapter!!.chapterId }
            if (curIndex < 0) return false
            logD(CLASS_TAG, "hasNextChapter curIndex:$curIndex")
            val nextIndex = if (isNext) curIndex + 1 else curIndex - 1
            return if (nextIndex in menuList.indices) {
                nextChapter =
                    mViewModel.getChapter(bookBaseInfo!!.bookId, menuList[nextIndex].chapterId)
                nextChapter?.apply {
                    if (content.isNullOrEmpty()) {
                        downloadChapter(chapterId, true)
                        isAutoTurnNext = isNext
                    }
                }
                true
            } else {
                nextChapter = null
                false
            }
        }
    }
}