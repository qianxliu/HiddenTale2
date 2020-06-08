package com.yanze.cloudreaderkotlin.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.yanze.cloudreaderkotlin.R
import com.youth.banner.loader.ImageLoader

class GlideImageLoader : ImageLoader() {
    override fun displayImage(context: Context?, url: Any?, imageView: ImageView?) {
        if (context != null) {
            if (imageView != null) {
                Glide.with(context).load(url)
                        .placeholder(R.drawable.shape_bg_loading)
                        .error(R.drawable.shape_bg_loading)
                        .transition(withCrossFade(100))
                        .into(imageView)
            }
        }
    }
}