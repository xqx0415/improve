package com.example.myhencoderdemo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.myhencoderdemo.R
import com.example.myhencoderdemo.dp

class CircleImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val imageSize = 300.dp
    private val padding = 20.dp
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bounds = RectF()

    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bounds.set(padding, padding, padding + imageSize, padding + imageSize)
        //创建一个离屏缓冲块，在内部画圆和图像
        val saveLayer = canvas.saveLayer(bounds, null)
        paint.color = Color.parseColor("#00FF40")
        canvas.drawOval(padding, padding, padding + imageSize, padding + imageSize, paint)
        //设置重叠的模式
        paint.xfermode = xfermode
        canvas.drawBitmap(getAvatar(imageSize.toInt()), padding, padding, paint)
        paint.xfermode = null
        canvas.restoreToCount(saveLayer)
    }

    private fun getAvatar(width: Int): Bitmap {
        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable.avatar, option)
        option.inJustDecodeBounds = false
        option.inDensity = option.outWidth
        option.inTargetDensity = width
        return BitmapFactory.decodeResource(resources, R.drawable.avatar, option)
    }

}