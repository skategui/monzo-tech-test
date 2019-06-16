package com.monzo.androidtest.ui.list.adapter

import com.monzo.androidtest.R
import com.monzo.androidtest.model.ItemArticle
import com.monzo.androidtest.ui.utils.simpleFormat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_article.*
import kotlinx.android.synthetic.main.item_date.tvDate

/**
 * Item in the recyclerview using groupie lib
 */
class ArticleItem(val article: ItemArticle) : Item() {

    override fun getLayout() = R.layout.item_article

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvArticleHeadline.text = article.title
        viewHolder.ivArticleThumbnail.loadRoundedImage(article.thumbnail)
        viewHolder.tvDate.text = article.published.simpleFormat()
    }
}