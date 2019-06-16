package com.monzo.androidtest.ui.list.adapter

import com.monzo.androidtest.R
import com.monzo.androidtest.ui.utils.simpleFormat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_date.*
import java.util.*

/**
 * Title for each section in the reyclerview.
 */
class TitleItem(private val title: String) : Item() {

    override fun getLayout() = R.layout.item_date

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvDate.text = title
    }
}