package com.monzo.androidtest.usecase

import com.monzo.androidtest.model.Article
import com.monzo.androidtest.model.ArticleDetail
import io.reactivex.Single

interface ArticleUseCase {
    /**
     * Load a list of articles from the server
     *  @return [Single]  [List]  [Article] list of articles found
     */
    fun getArticles(): Single<List<Article>>

    /**
     * Get the detail of a news given it's URL
     *  @return [Single]  [ArticleDetail] detail of the article, containing its content
     */

    fun getDetailArticle(articleUrl: String): Single<ArticleDetail>
}