package com.monzo.androidtest.repo

import com.monzo.androidtest.model.Article
import com.monzo.androidtest.model.ArticleDetail
import io.reactivex.Single

interface ArticleRepository {

    /**
     * Get the latest fintech news
     *  @return [Single]  [List]  [Article] list of articles found
     */
    fun getDetailArticle(articleUrl: String): Single<ArticleDetail>

    /**
     * Get the detail of a news given it's URL
     *  @return [Single]  [ArticleDetail] detail of the article, containing its content
     */
    fun latestFintechArticles(): Single<List<Article>>
}