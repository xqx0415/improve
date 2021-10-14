package com.example.myhencoderdemo

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.View


val Int.dp
    get() = this.toFloat().dp

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

fun View.getAvatar(width:Int):Bitmap{
    val option = BitmapFactory.Options()
    option.inJustDecodeBounds = true
    BitmapFactory.decodeResource(resources,R.drawable.avatar,option)
    option.inJustDecodeBounds = false
    option.inDensity = option.outWidth
    option.inTargetDensity = width
    return BitmapFactory.decodeResource(resources,R.drawable.avatar,option)
}
