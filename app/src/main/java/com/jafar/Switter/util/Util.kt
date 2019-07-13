package com.jafar.Switter.util

import android.app.DownloadManager
import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.bumptech.glide.request.RequestOptions
import com.jafar.Switter.R
import java.text.DateFormat
import java.util.*


//used to load images using the glide dependency , glide creates a cache of the image
fun ImageView.loadUrl(url:String?,errorDrawable:Int= R.drawable.empty)
{
    context?.let {
        val options =RequestOptions()
            .placeholder(progressDrawabale(context))
            .error(errorDrawable)
            Glide.with(context.applicationContext)
                .load(url)
                .apply(options)
                .into(this)
    }
}

fun progressDrawabale(context: Context):CircularProgressDrawable
{
    return CircularProgressDrawable(context).apply {
        strokeWidth=5f
        centerRadius=30f
        start()
    }
}

//function to convert the date in milli second to normal format
fun getDate(s:Long?):String
{
    s?.let {
        val df=DateFormat.getDateInstance()
        return df.format(Date(it))
    }
    return "Unknown"
}