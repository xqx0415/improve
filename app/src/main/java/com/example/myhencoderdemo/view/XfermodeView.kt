package com.example.myhencoderdemo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.myhencoderdemo.dp

class XfermodeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val width = 200.dp
    private val height = 200.dp
    private val circleBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(),Bitmap.Config.ARGB_8888)
    private val squareBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(),Bitmap.Config.ARGB_8888)

    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    init {
        val canvas = Canvas(circleBitmap)
        paint.color = Color.parseColor("#00FF40")
        canvas.drawOval(50f.dp,0f,width,150f.dp,paint)
        canvas.setBitmap(squareBitmap)
        paint.color = Color.parseColor("#008040")
        canvas.drawRect(0f,100f.dp,100f.dp,height,paint)

    }

    private val bounds = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bounds.set(0f, 0f, width, height)
        //创建一个离屏缓冲块，在内部画圆和图像
        val saveLayer = canvas.saveLayer(bounds, null)
        canvas.drawBitmap(circleBitmap,0f,0f,paint)
        paint.xfermode = xfermode
        canvas.drawBitmap(squareBitmap,0f,0f,paint)
        paint.xfermode = null
        canvas.restoreToCount(saveLayer)
    }

}