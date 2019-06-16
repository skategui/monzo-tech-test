package com.monzo.androidtest.repo

import com.monzo.androidtest.api.GuardianService
import com.monzo.androidtest.model.Article
import com.monzo.androidtest.model.ArticleDetail
import io.reactivex.Single
import javax.inject.Inject

/**
 * Responsible for making the requests to the server to get the data
 *  todo in the future: use a DataSource instead of calling a RetrofitService  directly
 */
class ArticleRepositoryImpl
@Inject constructor(private val guardianService: GuardianService) : ArticleRepository {

    /**
     * Get the latest fintech news
     *  @return [Single]  [List]  [Article] list of articles found
     */
    override fun latestFintechArticles(): Single<List<Article>> {
        return guardianService.searchArticles(FINTECH_SEARCH_TERMS, 50)
            .map { it.response.results }
            .toObservable()
            .flatMapIterable { it }
            .map { article ->
                Article(
                    id = article.id,
                    thumbnail = article.fields?.thumbnail ?: DEFAULT_VALUE,
                    sectionId = article.sectionId,
                    sectionName = article.sectionName,
                    published = article.webPublicationDate,
                    title = article.fields?.headline ?: DEFAULT_VALUE,
                    url = article.apiUrl
                )
            }
            .toList()
    }

    /**
     * Get the detail of a news given it's URL
     *  @return [Single]  [ArticleDetail] detail of the article, containing its content
     */
    override fun getDetailArticle(articleUrl: String): Single<ArticleDetail> {
        return guardianService.getArticle(articleUrl, ARTICLE_DETAIL_FIELDS_NEEDED)
            .map { it.response.content }
            .map { article ->
                ArticleDetail(
                    id = article.id,
                    thumbnail = article.fields?.thumbnail ?: DEFAULT_VALUE,
                    title = article.fields?.headline ?: DEFAULT_VALUE,
                    bodyHtml = article.fields?.body ?: DEFAULT_VALUE
                )
            }
    }


    companion object {
        private const val DEFAULT_VALUE = ""
        private const val FINTECH_SEARCH_TERMS = "fintech,brexit"
        private const val ARTICLE_DETAIL_FIELDS_NEEDED = "main,body,headline,thumbnail"
    }


}