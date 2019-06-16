package com.monzo.androidtest.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

/**
 * Responsible of loading external images , from URL, found in a textview
 *  @param textView textView containing potentially some images
 *  @param context current context
 */
class GlideImageGetter(private val textView: TextView, private val context: Context) : Html.ImageGetter {
    override fun getDrawable(source: String): Drawable {
        val drawable = LevelListDrawable()
        Glide.with(context)
            .asBitmap()
            .load(source)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val bitmapDrawable = BitmapDrawable(resource)
                    drawable.addLevel(1, 1, bitmapDrawable)
                    drawable.setBounds(0, 0, resource.width, resource.height)
                    drawable.level = 1
                    textView.invalidate()
                    textView.text = textView.text
                }
            })
        return drawable
    }

}