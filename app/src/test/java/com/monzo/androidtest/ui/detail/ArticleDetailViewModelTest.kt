package com.monzo.androidtest.ui.detail


import com.google.common.truth.Truth.assertThat
import com.monzo.androidtest.DataBuilder
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.ui.utils.BaseRule
import com.monzo.androidtest.usecase.ArticleUseCase
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

/**
 * BaseRule is Used to test async code in RxAndroid in the test
 */
class ArticleDetailViewModelTest : BaseRule() {

    private lateinit var viewModel: ArticleDetailViewModel
    private val articleUseCase = mock<ArticleUseCase>()

    private val article = DataBuilder.provideArticleDetail()


    @Before
    fun setUp() {
        viewModel = ArticleDetailViewModel(articleUseCase, HttpErrorUtils())
        whenever(articleUseCase.getDetailArticle(any())).thenReturn(Single.just(article))
    }

    @Test
    fun `Given the set of a post of the viewModel then emit this post`() {
        val testObserver = viewModel.viewStateObservable.test()
        viewModel.loadArticleFromURL("article_url")
        val loadingState = testObserver
            .assertNoErrors()
            .values()[0]

        val renderArticleState = testObserver
            .assertNoErrors()
            .values()[1]

        assertThat(loadingState).isEqualTo(ArticleDetailViewState.ShowLoading)
        assertThat(renderArticleState.cast<ArticleDetailViewState.RenderArticleDetail>().article).isEqualTo(article)
    }


    @Test
    fun `Given an error when loading the comments then send an error`() {
        val errorMsg = "errorMsg"
        whenever(articleUseCase.getDetailArticle(any())).thenReturn(Single.error(Throwable(errorMsg)))

        val testObserver = viewModel.viewStateObservable.test()

        viewModel.loadArticleFromURL("article_url")

        val state = testObserver
            .assertNoErrors()
            .values()[1]

        assertThat(state.cast<ArticleDetailViewState.Error>().error).isEqualTo(errorMsg)
    }

    @Test
    fun `Given no internet connexion when loading the comments then emit No internet action`() {
        whenever(articleUseCase.getDetailArticle(any())).thenReturn(Single.error(UnknownHostException()))

        val testObserver = viewModel.viewStateObservable.test()

        viewModel.loadArticleFromURL("article_url")

        testObserver
            .assertNoErrors()
            .assertValueAt(1, ArticleDetailViewState.NoInternet)
    }

}

internal fun <T> ArticleDetailViewState.cast(): T = this as T