package com.example.firebase_chat_demo.binding

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.firebase_chat_demo.R
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("android:setCircleImage")
fun setImageFromString(circleImageView: CircleImageView, imageUrl: String?) {
    if (imageUrl.isNullOrEmpty() || imageUrl == "default") {
        circleImageView.setImageResource(R.mipmap.ic_launcher)
    } else {
        Glide.with(circleImageView.context).load(imageUrl).into(circleImageView)
    }
}