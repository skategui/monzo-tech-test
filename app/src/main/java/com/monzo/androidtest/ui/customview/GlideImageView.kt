package com.monzo.androidtest.ui.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * Custom view to encapsulate Glide utilisation
 */
class GlideImageView : AppCompatImageView {

    private var requestOptions = RequestOptions().apply {
        transform(CenterCrop(), RoundedCorners(roundingRadius))
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    /**
     * load an image from an external url and add rounded corners
     *  @param url url of the image to load
     */
    fun loadRoundedImage(url: String) {
        Glide.with(context)
            .load(url)
            .apply(requestOptions)
            .into(this)
    }

    /**
     * load an image from an external url
     *  @param url url of the image to load
     */
    fun loadImage(url: String) {
        Glide.with(context)
            .load(url)
            .into(this)
    }


    companion object {

        // radius of the image
        private const val roundingRadius = 90
    }


}
