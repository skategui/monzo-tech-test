package com.monzo.androidtest.ui.detail

import android.app.Activity
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.monzo.androidtest.DataBuilder
import com.monzo.androidtest.GuardianApp
import com.monzo.androidtest.R
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.model.ArticleDetail
import com.monzo.androidtest.usecase.ArticleUseCase
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.DispatchingAndroidInjector_Factory
import io.reactivex.Single
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.UnknownHostException
import javax.inject.Provider

@RunWith(AndroidJUnit4::class)
class ArticleDetailActivityTest {
    private val articleUseCase = mock<ArticleUseCase>()

    private val testViewModelFactory = ArticleDetailViewModel.Factory(articleUseCase, HttpErrorUtils())

    @get:Rule
    val activityTestRule = ActivityTestRule<ArticleDetailActivity>(ArticleDetailActivity::class.java, false, false)

    private fun initDispatcherAndLaunchActivity(intent: Intent? = null) {
        val myApp =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GuardianApp
        myApp.dispatchingAndroidInjector = createFakeActivityInjector {
            viewModelFactory = testViewModelFactory
        }
        activityTestRule.launchActivity(intent)
    }


    @Test
    fun given_a_connexion_when_fetching_comments_then_show_comment_btn_with_the_popup() {

        val intent = Intent()

        val body = "body"
        val article = DataBuilder.getArticles().first()
        val detail = ArticleDetail(
            id = article.id,
            thumbnail = article.thumbnail,
            title = article.title,
            bodyHtml = body
        )
        whenever(articleUseCase.getDetailArticle(any())).thenReturn(Single.just(detail))

        intent.putExtra(ArticleDetailActivity.ARTICLE_URL_SELECTED, article.url)
        initDispatcherAndLaunchActivity(intent)

        onView(withId(R.id.favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvBody)).check(matches(isDisplayed()))

        onView(withId(R.id.tvTitle)).check(matches(withText(detail.title)))
        onView(withId(R.id.tvBody)).check(matches(withText(detail.bodyHtml)))
    }


    @Test
    fun given_an_error_of_connexion_when_fetching_detail_then_show_error_msg() {

        whenever(articleUseCase.getDetailArticle(any())).thenReturn(Single.error(UnknownHostException()))

        val intent = Intent()

        intent.putExtra(ArticleDetailActivity.ARTICLE_URL_SELECTED, "url")
        initDispatcherAndLaunchActivity(intent)

        onView(withId(R.id.llState)).check(matches(not(isDisplayed())))
    }

    // todo : add test to check if the favorite is turning on/off

    // utils
    private fun createFakeActivityInjector(block: ArticleDetailActivity.() -> Unit)
            : DispatchingAndroidInjector<Activity> {
        val injector = AndroidInjector<Activity> { instance ->
            if (instance is ArticleDetailActivity) {
                instance.block()
            }
        }
        val factory = AndroidInjector.Factory<Activity> { injector }
        val map = mapOf(
            Pair<Class<*>,
                    Provider<AndroidInjector.Factory<*>>>(ArticleDetailActivity::class.java, Provider { factory })
        )
        return DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(map, emptyMap(), emptyMap(), emptyMap())
    }
}