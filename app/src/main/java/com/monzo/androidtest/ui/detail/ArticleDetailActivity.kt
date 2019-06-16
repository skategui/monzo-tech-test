package com.monzo.androidtest.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieDrawable
import com.monzo.androidtest.R
import com.monzo.androidtest.common.DaggerBaseActivity
import com.monzo.androidtest.model.ArticleDetail
import com.monzo.androidtest.ui.utils.GlideImageGetter
import com.monzo.androidtest.ui.utils.io
import kotlinx.android.synthetic.main.activity_article_detail.*
import javax.inject.Inject

/**
 * View associated to the detail of an article
 */
class ArticleDetailActivity : DaggerBaseActivity() {

    @Inject
    lateinit var viewModelFactory: ArticleDetailViewModel.Factory


    private lateinit var viewModel: ArticleDetailViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)
        getArticleURLFromIntent()
    }

    /**
     * Get the article URL from the intent , if not found close the view
     */
    private fun getArticleURLFromIntent() {
        val articleURL = intent?.extras?.get(ARTICLE_URL_SELECTED) as String?

        articleURL?.let {
            initView()
            initViewModel(it)
        } ?: finish()
    }

    /**
     * Init the view
     */
    private fun initView() {
        initToolbar()
        initAnimation()
    }

    /**
     * Init toolbar
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * Init animation of the comment button to push the user to click on it
     */
    private fun initAnimation() {
        // set animation
        animation.repeatMode = LottieDrawable.REVERSE
        animation.repeatCount = LottieDrawable.INFINITE
    }


    /**
     * init viewmodel with the articleURL of the article to display
     * @param articleURL of the article to display
     */
    private fun initViewModel(articleURL: String) {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ArticleDetailViewModel::class.java)
        lifecycle.addObserver(viewModel)
        viewModel.loadArticleFromURL(articleURL)
        disposables.add(
            viewModel.viewStateObservable
                .io()
                .subscribe(this::render, Throwable::printStackTrace)
        )

    }

    /**
     * Render the view with the actions given the current state of the view
     * @param detailViewState current state of the view
     */
    private fun render(detailViewState: ArticleDetailViewState) {
        checkLoadingState(false)
        when (detailViewState) {
            is ArticleDetailViewState.RenderArticleDetail -> {
                renderArticleInfo(detailViewState.article)
            }
            is ArticleDetailViewState.NoInternet -> noInternet()
            is ArticleDetailViewState.Error -> displayErrorMsg()
            is ArticleDetailViewState.ShowLoading -> checkLoadingState(true)
        }
    }

    /**
     * Check the state of the loading animation
     * @param isLoading true if the request is still loading, false otherwise
     */
    private fun checkLoadingState(isLoading: Boolean) {
        if (isLoading) {
            animation.setAnimation(R.raw.loading_animation)
            animation.playAnimation()
            tvStateTitle.text = getString(R.string.loading_in_progress)
            llState.visibility = View.VISIBLE
        } else {
            llState.visibility = View.GONE
        }
    }


    /**
     * Render the section associated to the article info
     * @param article article to display the info from
     */
    private fun renderArticleInfo(article: ArticleDetail) {
        favorite.setArticle(article.id)
        tvTitle.text = article.title
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvBody.text = Html.fromHtml(article.bodyHtml, GlideImageGetter(tvBody, this), null)
        } else {
            tvBody.text = Html.fromHtml(article.bodyHtml)
        }
        ivThumbnail.loadImage(article.thumbnail)
    }


    /**
     * Inform the user to the error
     */
    private fun displayErrorMsg() {
        Toast.makeText(this, getString(R.string.error_try_again_later), Toast.LENGTH_LONG)
            .show()
    }


    /**
     * Inform the user to that he lost the internet connexion
     */
    private fun noInternet() {
        Toast.makeText(this, getString(R.string.lost_internet), Toast.LENGTH_LONG)
            .show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


    companion object {

        @VisibleForTesting
        const val ARTICLE_URL_SELECTED = "article_url_selected"

        /**
         * Start the activity with the article associated to the view
         * @param context current context
         * @param articleURL article URL to the article to display
         */
        fun start(context: Context, articleURL: String) {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(ARTICLE_URL_SELECTED, articleURL)
            context.startActivity(intent)

        }
    }


}
