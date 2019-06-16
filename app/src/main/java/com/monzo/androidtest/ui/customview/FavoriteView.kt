package com.monzo.androidtest.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.monzo.androidtest.GuardianApp
import com.monzo.androidtest.R
import com.monzo.androidtest.datastore.FavoriteDataStore
import com.varunest.sparkbutton.SparkEventListener
import kotlinx.android.synthetic.main.custom_favorite.view.*
import javax.inject.Inject


/**
 * Custom view that handle the add/remove from favorite
 */
class FavoriteView : LinearLayout {

    private var articleID: String? = null

    @Inject
    lateinit var favoriteDataStore: FavoriteDataStore


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        GuardianApp.guardianAppComponent.inject(this)
        View.inflate(context, R.layout.custom_favorite, this)


        spark_button.setEventListener(object : SparkEventListener {
            override fun onEvent(button: ImageView?, buttonState: Boolean) {
                articleID?.run {
                    onFavoriteClicked(buttonState, this)
                }
            }

            override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {
            }

            override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {
            }

        })
    }

    /**
     * Set the article ID  that will be added/removed from the favorite
     * @param articleID ID of the article selected
     */
    fun setArticle(articleID: String) {
        this.articleID = articleID
        spark_button.isChecked = favoriteDataStore.isAddedToFavorite(articleID)
    }

    /**
     * Check if the article ID needs to be added or removed from the favourite
     * @param isEnable true if the article needs to be add, false if it needs to be removed
     * @param articleID ID of the article selected
     */
    private fun onFavoriteClicked(isEnable: Boolean, articleID: String) {
        if (isEnable)
            addToFavorite(articleID)
        else
            removeFavorite(articleID)
    }

    /**
     * Remove article from the favourite
     * @param articleID ID of the article to rmove
     */
    private fun removeFavorite(articleID: String) {
        favoriteDataStore.removeFavorite(articleID)
        Toast.makeText(context, context.getString(R.string.favorite_remove_msg), Toast.LENGTH_SHORT).show()

    }

    /**
     * Add article to the favourite
     * @param articleID ID of the article to add
     */
    private fun addToFavorite(articleID: String) {
        favoriteDataStore.addFavorite(articleID)
        Toast.makeText(context, context.getString(R.string.favorite_add_msg), Toast.LENGTH_SHORT).show()
    }

}
