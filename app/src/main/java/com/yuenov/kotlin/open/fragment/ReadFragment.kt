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
import com.yuenov.kotlin.open.constant.PreferenceConstants.KEY_NOW_READING_BOOK_ID
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.databinding.FragmentReadBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.utils.BrightnessUtils
import com.yuenov.kotlin.open.utils.DataStoreUtils
import com.yuenov.kotlin.open.utils.ScreenUtils
import com.yuenov.kotlin.open.utils.SystemBarUtils
import com.yuenov.kotlin.open.viewmodel.BookShelfFragmentViewModel
import com.yuenov.kotlin.open.widget.mypage.PageAnimationType
import com.yuenov.kotlin.open.widget.mypage.PageBackground
import com.yuenov.kotlin.open.widget.mypage.PageOperationView
import com.yuenov.kotlin.open.widget.mypage.ReadSettingInfo
import com.yuenov.kotlin.open.widget.page.IPagerLoader
import com.yuenov.kotlin.open.widget.page.animation.HorizonPageAnimation
import com.yuenov.kotlin.open.widget.page.animation.PageAnimation
import me.hgj.jetpackmvvm.ext.nav

/**
 * TODO:阅读界面
 */
class ReadFragment : BaseFragment<BookShelfFragmentViewModel, FragmentReadBinding>() {

    private var bookBaseInfo: BookBaseInfo? = null
    private var chapterId: Long? = 0
    private var menuList: List<TbBookChapter> = listOf()
    private var hasBookShelf: Boolean = false
    private val readSettingInfo: ReadSettingInfo = DataStoreUtils.getJsonData(
        PreferenceConstants.KEY_READ_SETTING_INFO,
        ReadSettingInfo::class.java
    )
    private var pageLoader: IPagerLoader? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                pageLoader?.updateBattery(level)
            } else if (intent?.action == Intent.ACTION_TIME_TICK) {
                pageLoader?.updateTime()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            setClickListener(
                rlDiTop,
                tvDiLeft,
                tvDiDownLoad,
                tvDiUpdateContent,
                tvDiAddShelf
            ) { view ->
                if (isLoadingShowing()) return@setClickListener
                when (view) {
                    rlDiTop -> if (rlDiTop.isVisible) dovDiOperation.hideAllContent()
                    tvDiLeft -> {
                        resetVisibility(false, rlDiTop)
                        onBackPressed()
                    }
                    tvDiAddShelf -> addBookShelf()
                    tvDiUpdateContent -> updateContent()
                    tvDiDownLoad -> toDownload()
                }
            }
            // TODO 还需要加上对应PageView操作
            dovDiOperation.setListener(object : PageOperationView.PageOperationViewListener {
                override fun <T> onDataChange(event: Int, newValue: T) {
                    when (event) {
                        PageOperationView.DATA_CHANGE_EVENT_BG_TYPE -> {
                            val bgType = newValue as PageBackground
                            if (readSettingInfo.bgColor != bgType) {
                                readSettingInfo.bgColor = bgType
                                pageLoader?.setBgColor(bgType.bgColor)
                                pageLoader?.setTextColor(bgType.textColor)
                            }
                        }
                        PageOperationView.DATA_CHANGE_EVENT_LIGHT_VALUE -> {
                            val lightValue = newValue as Int
                            if (readSettingInfo.lightValue != lightValue) {
                                readSettingInfo.lightValue = lightValue
                            }
                        }
                        PageOperationView.DATA_CHANGE_EVENT_FONT_SIZE -> {
                            val fontSize = newValue as Float
                            if (readSettingInfo.fontSize != fontSize) {
                                readSettingInfo.fontSize = fontSize
                                pageLoader?.setFontSize(fontSize)
                            }
                        }
                        PageOperationView.DATA_CHANGE_EVENT_PAGE_ANIM_TYPE -> {
                            val animType = newValue as PageAnimationType
                            if (readSettingInfo.pageAnimType != animType) {
                                readSettingInfo.pageAnimType = animType
//                                pageLoader?.setAnimation(HorizonPageAnimation())
                            }
                        }
                    }
                    DataStoreUtils.putJsonData(PreferenceConstants.KEY_READ_SETTING_INFO, readSettingInfo)
                }

                override fun onSelectChapter(chapterId: Long) {
                    if (chapterId < 1) return
                    hideOperation()
                    // TODO 打开对应章节
                    this@ReadFragment.chapterId = chapterId
                    pageLoader?.openChapter(chapterId)
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
        chapterId = arguments?.getLong(PreferenceConstants.EXTRA_LONG_CHAPTER_ID)
        hasBookShelf = arguments?.getBoolean(PreferenceConstants.EXTRA_BOOL_HAS_BOOKSHELF) == true

        if (bookBaseInfo == null || bookBaseInfo!!.bookId < 1) {
            showToast("未知数据")
            nav().navigateUp()
        }

        mViewBind.apply {
            if (readSettingInfo.lightValue > 1) {
                BrightnessUtils.setAppScreenBrightness(
                    requireActivity(),
                    readSettingInfo.lightValue
                )
            }
            if (hasBookShelf) resetVisibility(!hasBookShelf, tvDiAddShelf)

            dovDiOperation.init(
                bookBaseInfo!!.title ?: "",
                menuList,
                chapterId ?: 0,
                readSettingInfo.bgColor,
                readSettingInfo.lightValue,
                readSettingInfo.fontSize,
                readSettingInfo.pageAnimType
            )
        }

        initPageLoader()
        registerReceiver()
    }

    override fun needBackPressedCallback(): Boolean {
        return true
    }

    override fun onBackPressed() {
        logd(CLASS_TAG, "onBackPressed")
        super.onBackPressed()

        if (isLoadingShowing()) dismissLoading()
        // 正常退出清掉最后阅读的图书
        DataStoreUtils.putData(KEY_NOW_READING_BOOK_ID, "")
    }

    // TODO
    private fun initPageLoader() {

    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    private fun unregisterReceiver() {
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        SystemBarUtils.hideStableStatusBar(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterReceiver()
    }

    private fun addBookShelf() {

    }

    private fun updateContent() {

    }

    private fun hideOperation() {
        mViewBind.apply {
            dovDiOperation.hideAllContent()
            setAnimation(R.anim.slide_top_out, rlDiTop, false)
            setAnimation(R.anim.slide_bottom_out, dovDiOperation, false)
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
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (isShow) {
                    SystemBarUtils.showUnStableStatusBar(requireActivity())
                } else {
                    SystemBarUtils.hideStableStatusBar(requireActivity())
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        resetVisibility(isShow, view)
        view.startAnimation(animation)
    }
}