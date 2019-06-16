package com.monzo.androidtest

import com.monzo.androidtest.model.Article
import com.monzo.androidtest.model.ArticleDetail
import java.time.Instant
import java.util.*

object DataBuilder {

    fun getArticles(): List<Article> {
        return listOf(
            Article("id1", "thumb1", "sec1", "secName", Date.from(Instant.now().minusSeconds(10000)), "title1", "url1"),
            Article("id2", "thumb2", "sec2", "secName", Date.from(Instant.now().minusSeconds(40000)), "title2", "url2"),
            Article("id3", "thumb3", "sec3", "secName", Date.from(Instant.now().minusSeconds(50000)), "title3", "url3"),
            Article("id4", "thumb4", "sec4", "secName", Date.from(Instant.now().minusSeconds(60000)), "title4", "url4")
        )
    }

    fun provideArticleDetail() = ArticleDetail(
        "id1", "thumb1", "title", "bodyHTML"
    )
}