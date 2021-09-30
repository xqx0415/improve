package com.example.myhencoderdemo

import android.content.res.Resources
import android.util.TypedValue


val Int.dp
    get() = this.toFloat().dp

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )