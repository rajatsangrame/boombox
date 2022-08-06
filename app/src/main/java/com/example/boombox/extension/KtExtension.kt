package com.example.boombox.extension

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.boombox.R

fun <T> Call<T>.enqueue(
  success: (response: Response<T>) -> Unit,
  error: (errorResponse: Response<T>) -> Unit,
  failure: (t: Throwable) -> Unit
) {
  enqueue(object : Callback<T> {
    override fun onResponse(
      call: Call<T>?,
      response: Response<T>
    ) {
      if (response.isSuccessful) {
        success(response)
      } else {
        error(response)
      }
    }

    override fun onFailure(
      call: Call<T>?,
      t: Throwable
    ) {
      failure(t)
    }
  })
}

fun ImageView.loadImage(url: String) {
  val color = ContextCompat.getDrawable(this.context, R.color.cardBackground)
  Glide.with(this.context)
    .load(url)
    .placeholder(color)
    .into(this)
}



