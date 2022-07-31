package com.yuenov.kotlin.open.widget.page

import com.yuenov.kotlin.open.database.tb.TbBookChapter

interface IPagerLoader {
    fun onPreChapter(newBookChapter: TbBookChapter)
    fun onNextChapter(newBookChapter: TbBookChapter)
    fun onTurnPage()
    fun showAd()
}