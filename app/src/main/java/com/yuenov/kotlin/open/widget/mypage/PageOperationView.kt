package com.yuenov.kotlin.open.widget.mypage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.AnimRes
import androidx.core.view.isVisible
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.DetailBottomMenuListAdapter
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.databinding.ViewWidgetDetailoperationBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.utils.ConvertUtils
import com.yuenov.kotlin.open.widget.LightView

class PageOperationView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), View.OnClickListener, LightView.LightViewListener,
    SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {

    private val minTextSize = 16f
    private val maxTextSize = 32f

    private var binding: ViewWidgetDetailoperationBinding
    private val contentViews: Array<Pair<ViewGroup, ImageView>>
    private val selectedImages = arrayOf(
        R.mipmap.ic_book_menu_select,
        R.mipmap.ic_book_progress_select,
        R.mipmap.ic_book_light_select,
        R.mipmap.ic_book_set_select
    )
    private val unselectedImages = arrayOf(
        R.mipmap.ic_book_menu_unselect,
        R.mipmap.ic_book_progress_unselect,
        R.mipmap.ic_book_light_unselect,
        R.mipmap.ic_book_set_unselect
    )
    private val animViews: Array<TextView>
    private val lightViews: Array<LightView>

    // 目录界面书籍名称
    private var title: String = ""

    // 当前章节ID
    private var curChapterId: Long = 0
    private var menuListOrderAsc: Boolean = true

    // TODO:
    // 使用TbBookChapter，当文本量过大时，是否会造成IO堵塞，以及内存占用过大，
    // 实际用到的也只有chapterId和chapterName这两个字段
    private var menuList: List<TbBookChapter> = listOf()
    private var menuListAdapter: DetailBottomMenuListAdapter
    private var listener: PageOperationViewListener? = null
    // 背景颜色+字体颜色
    private var bgType: PageBackground = PageBackground.TYPE_1

    // 亮度值
    private var lightValue: Int = 0

    // 字体大小
    private var textSize: Float = 18f

    // 翻页动画类型
    private var pageAnimType: PageAnimationType = PageAnimationType.SIMULATION

    init {
        binding = ViewWidgetDetailoperationBinding.inflate(LayoutInflater.from(context))

        binding.apply {
            addView(root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            contentViews = arrayOf(
                Pair(llWgDpMenuListData, ivWgDpMenu),
                Pair(llWgDpProcessContent, ivWgDpProcess),
                Pair(llWgDpLightContent, ivWgDpLight),
                Pair(llWgDpFontContent, ivWgDpFont)
            )
            animViews = arrayOf(
                tvWgDpAnim1,
                tvWgDpAnim2,
                tvWgDpAnim3,
                tvWgDpAnim4
            )
            lightViews = arrayOf(
                lvWgDpLight1,
                lvWgDpLight2,
                lvWgDpLight3,
                lvWgDpLight4,
                lvWgDpLight5
            )
            val screenHeight = this@PageOperationView.context.resources.displayMetrics.heightPixels
            lvWgDpMenuList.setMaxHeight(screenHeight - ConvertUtils.dp2px(165f))
            menuListAdapter = DetailBottomMenuListAdapter(menuList)
            lvWgDpMenuList.adapter = menuListAdapter
        }
    }

    fun init(
        title: String,
        menuList: List<TbBookChapter>,
        chapterId: Long,
        bgType: PageBackground,
        lightValue: Int,
        textSize: Float,
        pageAnimType: PageAnimationType
    ) {
        setTitle(title)
        setMenuList(menuList)
        setChapterId(chapterId)
        setBgType(bgType)
        setLightValue(lightValue)
        setTextSize(textSize)
        setPageAnimType(pageAnimType)

        initListener()
    }

    private fun initListener() {
        binding.apply {
            setClickListener(
                viewWgDpMenuListClose, rlWgDpMenuListOrder,
                llWgDpMenu, llWgDpProcess, llWgDpLight, llWgDpFont,
                tvWgDpAnim1, tvWgDpAnim2, tvWgDpAnim3, tvWgDpAnim4,
                listener = this@PageOperationView
            )
            lvWgDpMenuList.onItemClickListener = this@PageOperationView
            setSeekBarChangeListener(
                skWgDpProcess, skWgDpLight, skWgDpFont,
                listener = this@PageOperationView
            )
            setLightListener(
                lvWgDpLight1, lvWgDpLight2, lvWgDpLight3, lvWgDpLight4, lvWgDpLight5,
                listener = this@PageOperationView
            )
            addSeekBarTouchPoint(skWgDpLight, skWgDpFont)
        }
    }

    fun setTitle(title: String) {
        this.title = title
        binding.tvWgDpMenuListTitle.text = title
    }

    fun setMenuList(list: List<TbBookChapter>) {
        menuList = list
        menuListAdapter.data = menuList
        menuListAdapter.notifyDataSetChanged()
    }

    fun setChapterId(chapterId: Long) {
        curChapterId = chapterId
        val position = getMenuListPosition(getPositionByChapterId(chapterId))
        binding.lvWgDpMenuList.setSelection(position)
    }

    fun setBgType(type: PageBackground) {
        bgType = type
        selectBgType()
    }

    fun setLightValue(value: Int) {
        lightValue = value
        binding.skWgDpLight.progress = lightValue
    }

    fun setTextSize(size: Float) {
        textSize = size
        binding.skWgDpFont.max = ((maxTextSize - minTextSize) / 2).toInt()
        binding.skWgDpFont.progress = ((textSize - minTextSize) / 2).toInt()
    }

    fun setPageAnimType(type: PageAnimationType) {
        pageAnimType = type
        selectAnim()
    }

    fun setListener(lis: PageOperationViewListener) {
        listener = lis
    }

    private fun sortMenuList() {
        binding.apply {
            ivWgDpMenuListOrder.setImageResource(
                if (menuListOrderAsc) R.mipmap.ic_book_down else R.mipmap.ic_book_up
            )
            menuListAdapter.orderByAes = menuListOrderAsc
            // TODO: 先去除点击响应，等刷新完成后再添加，避免快速点击导致UI阻塞 ??
            setClickListener(ivWgDpMenuListOrder, listener = null)
            menuListAdapter.notifyDataSetChanged()
            lvWgDpMenuList.post {
                setClickListener(ivWgDpMenuListOrder, listener = this@PageOperationView)
                if (menuList.isNotEmpty()) lvWgDpMenuList.setSelection(0)
            }
        }
    }

    private fun selectAnim() {
        binding.apply {
            when (pageAnimType) {
                PageAnimationType.SIMULATION -> selectAnimView(tvWgDpAnim1)
                PageAnimationType.COVER -> selectAnimView(tvWgDpAnim2)
                PageAnimationType.SCROLL -> selectAnimView(tvWgDpAnim3)
                PageAnimationType.NONE -> selectAnimView(tvWgDpAnim4)
                PageAnimationType.SLIDE -> {}
            }
        }
    }

    private fun selectAnimView(selected: TextView) {
        for (i in animViews.indices) {
            val item = animViews[i]
            if (item == selected) {
                selected.setTextColor(resources.getColor(R.color.gray_0000, null))
                selected.setBackgroundResource(R.drawable.bg_widget_bd_op_select)
            } else {
                item.setTextColor(resources.getColor(R.color._5e60, null))
                item.setBackgroundResource(R.drawable.bg_widget_bd_op_unselect)
            }
        }
    }

    private fun selectBgType() {
        binding.apply {
            when (bgType) {
                PageBackground.TYPE_1 -> selectLightView(lvWgDpLight1)
                PageBackground.TYPE_2 -> selectLightView(lvWgDpLight2)
                PageBackground.TYPE_3 -> selectLightView(lvWgDpLight3)
                PageBackground.TYPE_4 -> selectLightView(lvWgDpLight4)
                PageBackground.TYPE_5 -> selectLightView(lvWgDpLight5)
            }
        }
    }

    private fun selectLightView(selected: LightView) {
        for (i in lightViews.indices) {
            val item = lightViews[i]
            if (item == selected) {
                item.setSelect(true)
            } else {
                item.setSelect(false)
            }
        }
    }

    private fun getPositionByChapterId(chapterId: Long): Int {
        var position = -1
        for (i in menuList.indices) {
            if (menuList[i].chapterId == chapterId) {
                position = i
                break
            }
        }
        return position
    }

    private fun getMenuListPosition(position: Int): Int {
        return if (menuList.isEmpty())
            0
        else
            if (menuListOrderAsc) position else menuList.size - 1
    }

    private fun showMenu() {
        logd(CLASS_TAG, "showMenu ${menuList.size}")
        if (menuList.isEmpty()) return
        showContentView(binding.llWgDpMenuListData, R.anim.anim_widget_bookdetail_bottomshow)
        showAnimation(binding.rlWgDpMenuList, R.anim.anim_fade_in)
    }

    private fun hideMenu() {
        logd(CLASS_TAG, "hideMenu")
        binding.apply {
            hideAnimation(rlWgDpMenuList, R.anim.anim_fade_out)
            hideContentView(
                llWgDpMenuListData,
                R.anim.anim_widget_bookdetail_bottomhide,
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        resetVisibility(false, llWgDpMenuListData)
                        if (!menuListOrderAsc) {
                            menuListOrderAsc = true
                            menuListAdapter.orderByAes = menuListOrderAsc
                            menuListAdapter.notifyDataSetChanged()
                            ivWgDpMenuListOrder.setImageResource(R.mipmap.ic_book_down)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
        }
    }

    private fun showProcess() {
        if (menuList.isEmpty()) return
        binding.apply {
            skWgDpProcess.max = menuList.size - 1
            val position = getPositionByChapterId(curChapterId)
            skWgDpProcess.progress = position
            tvWgDpProcessTitle.text = menuList[position].chapterName
            showContentView(llWgDpProcessContent, R.anim.anim_widget_bookdetail_bottomshow)
        }
    }

    private fun hideProcess() {
        hideContentView(binding.llWgDpProcessContent, R.anim.anim_widget_bookdetail_bottomhide)
    }

    private fun showLight() {
        showContentView(binding.llWgDpLightContent, R.anim.anim_widget_bookdetail_bottomshow)
    }

    private fun hideLight() {
        hideContentView(binding.llWgDpLightContent, R.anim.anim_widget_bookdetail_bottomhide)
    }

    private fun showFont() {
        showContentView(binding.llWgDpFontContent, R.anim.anim_widget_bookdetail_bottomshow)
    }

    private fun hideFont() {
        hideContentView(binding.llWgDpFontContent, R.anim.anim_widget_bookdetail_bottomhide)
    }

    fun isShowContent(): Boolean {
        binding.run {
            return (rlWgDpMenuList.isVisible)
                    || (llWgDpProcessContent.isVisible)
                    || (llWgDpLightContent.isVisible)
                    || (llWgDpFontContent.isVisible)
        }
    }

    fun hideAllContent() {
        hideMenu()
        hideProcess()
        hideLight()
        hideFont()
    }

    override fun onClick(v: View) {
        if (isFastDoubleClick() || isLoadingShowing()) return
        binding.apply {
            when(v) {
                viewWgDpMenuListClose -> hideMenu()
                llWgDpMenu -> if (rlWgDpMenuList.isVisible) hideMenu() else showMenu()
                llWgDpProcess -> if (llWgDpProcessContent.isVisible) hideProcess() else showProcess()
                llWgDpLight -> if (llWgDpLightContent.isVisible) hideLight() else showLight()
                llWgDpFont -> if (llWgDpFontContent.isVisible) hideFont() else showFont()
                rlWgDpMenuListOrder -> {
                    menuListOrderAsc = !menuListOrderAsc
                    sortMenuList()
                }
                tvWgDpAnim1, tvWgDpAnim2, tvWgDpAnim3, tvWgDpAnim4 -> {
                    when(v) {
                        tvWgDpAnim1 -> pageAnimType = PageAnimationType.SIMULATION
                        tvWgDpAnim2 -> pageAnimType = PageAnimationType.COVER
                        tvWgDpAnim3 -> pageAnimType = PageAnimationType.SCROLL
                        tvWgDpAnim4 -> pageAnimType = PageAnimationType.NONE
                    }
                    selectAnim()
                    listener?.onDataChange(DATA_CHANGE_EVENT_PAGE_ANIM_TYPE, pageAnimType)
                }
            }
        }
    }

    override fun onStateChange(view: View, select: Boolean) {
        binding.apply {
            if (select) {
                when(view) {
                    lvWgDpLight1 -> bgType = PageBackground.TYPE_1
                    lvWgDpLight2 -> bgType = PageBackground.TYPE_2
                    lvWgDpLight3 -> bgType = PageBackground.TYPE_3
                    lvWgDpLight4 -> bgType = PageBackground.TYPE_4
                    lvWgDpLight5 -> bgType = PageBackground.TYPE_5
                }
                selectBgType()
                listener?.onDataChange(DATA_CHANGE_EVENT_BG_TYPE, bgType)
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        binding.apply {
            when(seekBar) {
                skWgDpProcess -> tvWgDpProcessTitle.text = menuList[progress].chapterName
                skWgDpLight -> {
                    lightValue = progress
                    listener?.onDataChange(DATA_CHANGE_EVENT_LIGHT_VALUE, lightValue)
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        val progress = seekBar.progress
        binding.apply {
            when(seekBar) {
                skWgDpProcess -> {
                    val chapter = menuList[progress]
                    tvWgDpProcessTitle.text = chapter.chapterName
                    hideAllContent()
                    listener?.onSelectChapter(chapter.chapterId)
                }
                skWgDpLight -> {
                    lightValue = progress
                    listener?.onDataChange(DATA_CHANGE_EVENT_LIGHT_VALUE, lightValue)
                }
                skWgDpFont -> {
                    textSize = minTextSize + progress * 2
                    listener?.onDataChange(DATA_CHANGE_EVENT_FONT_SIZE, textSize)
                }
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        listener?.onSelectChapter(menuList[getMenuListPosition(position)].chapterId)
        hideAllContent()
    }

    interface PageOperationViewListener {
        // 数据发生变化
        fun <T> onDataChange(event: Int, newValue: T)
        // 选择了某一章
        fun onSelectChapter(chapterId: Long)
    }

    //----------------一些private函数，减少样板代码--------------
    private fun setLightListener(vararg views: LightView, listener: LightView.LightViewListener) {
        for (i in views.indices) {
            views[i].setListener(listener)
        }
    }

    private fun setSeekBarChangeListener(
        vararg views: SeekBar,
        listener: SeekBar.OnSeekBarChangeListener
    ) {
        for (i in views.indices) {
            views[i].setOnSeekBarChangeListener(listener)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    // 好像没啥用，虽然能将触摸事件传进去，但是触摸位置在扩展范围内时，thumb还是不会动，可能和seekbar内部的计算有关系
    private fun addSeekBarTouchPoint(vararg seekBar: SeekBar) {
        for (i in seekBar.indices) {
            val sb = seekBar[i]
            val parent = sb.parent as ViewGroup
            parent.setOnTouchListener { _, event ->
                val rect = Rect()
                sb.getHitRect(rect)
                logd(PageOperationView.CLASS_TAG, "rect=$rect, event=${event.x}, ${event.y}")
                if ((event.y >= (rect.top - 0)) && (event.y <= (rect.bottom + 0)) //){
                    && ((event.x >= rect.left) && (event.x <= rect.right))) {
                    val y = rect.top + rect.height() / 2.0f
//                    var x = event.x - rect.left
//                    if (x < 0f) {
//                        x = 0f
//                    } else if (x > rect.width()) {
//                        x = rect.width().toFloat()
//                    }
                    val me = MotionEvent.obtain(
                        event.downTime,
                        event.eventTime,
                        event.action,
//                        x,
                        event.x,
                        y,
                        event.metaState
                    )
                    sb.onTouchEvent(me)
                } else {
                    false
                }
            }
        }
    }

    private fun showContentView(
        contentView: ViewGroup,
        @AnimRes animResId: Int,
        listener: Animation.AnimationListener? = null
    ) {
        for (i in contentViews.indices) {
            val views = contentViews[i]
            if (views.first == contentView) {
                resetVisibility(true, views.first)
                views.second.setImageResource(selectedImages[i])
            } else {
                resetVisibility(false, views.first)
                views.second.setImageResource(unselectedImages[i])
            }
        }
        showAnimation(contentView, animResId, listener)
    }

    private fun hideContentView(
        contentView: ViewGroup,
        @AnimRes animResId: Int,
        listener: Animation.AnimationListener? = null
    ) {
        for (i in contentViews.indices) {
            val views = contentViews[i]
            if (views.first == contentView) {
                views.second.setImageResource(unselectedImages[i])
            }
        }
        hideAnimation(contentView, animResId, listener)
    }

    private fun showAnimation(
        contentView: ViewGroup,
        @AnimRes animResId: Int,
        listener: Animation.AnimationListener? = null
    ) {
        val showAnimation = AnimationUtils.loadAnimation(context, animResId)
        if (listener != null)
            showAnimation.setAnimationListener(listener)
        resetVisibility(true, contentView)
        contentView.startAnimation(showAnimation)
    }

    private fun hideAnimation(
        contentView: ViewGroup,
        @AnimRes animResId: Int,
        listener: Animation.AnimationListener? = null
    ) {
        if (!contentView.isVisible) return
        val hideAnimation = AnimationUtils.loadAnimation(context, animResId)
        if (listener != null) {
            hideAnimation.setAnimationListener(listener)
        } else {
            hideAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    resetVisibility(false, contentView)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        contentView.startAnimation(hideAnimation)
    }

    companion object {
        const val DATA_CHANGE_EVENT_BG_TYPE = 0
        const val DATA_CHANGE_EVENT_LIGHT_VALUE = 1
        const val DATA_CHANGE_EVENT_FONT_SIZE = 2
        const val DATA_CHANGE_EVENT_PAGE_ANIM_TYPE = 3
    }
}