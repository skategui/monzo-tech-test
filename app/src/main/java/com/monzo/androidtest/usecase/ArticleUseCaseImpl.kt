package com.monzo.androidtest.usecase


import com.monzo.androidtest.repo.ArticleRepository
import com.monzo.androidtest.model.Article
import com.monzo.androidtest.model.ArticleDetail
import io.reactivex.Single
import javax.inject.Inject

/**
 * Responsible for the business logic of an article.
 * Currently, does not do much apart from loading the data from the server. But in the future, all the business logic
 * for the Article should be here.
 */
class ArticleUseCaseImpl
@Inject constructor(private val articleRepository: ArticleRepository) : ArticleUseCase {

    /**
     * Load a list of articles from the server
     *  @return [Single]  [List]  [Article] list of articles found
     */
    override fun getArticles(): Single<List<Article>> {
        return articleRepository.latestFintechArticles()
    }

    /**
     * Get the detail of a news given it's URL
     *  @return [Single]  [ArticleDetail] detail of the article, containing its content
     */

    override fun getDetailArticle(articleUrl: String): Single<ArticleDetail> {
        return articleRepository.getDetailArticle(articleUrl)
    }


}