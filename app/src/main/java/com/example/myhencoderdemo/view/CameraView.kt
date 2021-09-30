package com.example.myhencoderdemo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withSave
import com.example.myhencoderdemo.R
import com.example.myhencoderdemo.dp

class CameraView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val TAG = "CameraView"

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //图片大小，为正方形图片
    private val avatarSize = 150.dp
    private var avatarPaddingLeft = 0f
    private var avatarPaddingTop = 0f

    private val camera = Camera()

    private var degrees = 0f
        set(value) {
            if (field != value){
                field = value
                invalidate()
            }
        }

    private var topRotateX = 0f
        set(value) {
            if (field != value){
                field = value
                invalidate()
            }
        }

    private var bottomRotateX = 0f
        set(value) {
            if (field != value){
                field = value
                invalidate()
            }
        }


    init {
        camera.setLocation(0f,0f,-7 * resources.displayMetrics.density)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        avatarPaddingLeft = (w - avatarSize)/2
//        avatarPaddingTop = (h - avatarSize)/2
        avatarPaddingTop = 50f.dp
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //画上半部分
        canvas.withSave {
            canvas.translate(avatarPaddingLeft + avatarSize/2, avatarPaddingTop + avatarSize/2)
            canvas.rotate(-degrees)
            camera.save()
            camera.rotateX(topRotateX)
            camera.applyToCanvas(canvas)
            camera.restore()
            canvas.clipRect(-avatarSize,-avatarSize,avatarSize,0f)
            //斜角度切，就是旋转画布
            canvas.rotate(degrees)
            canvas.translate(-(avatarPaddingLeft + avatarSize/2), -(avatarPaddingTop + avatarSize/2))
            canvas.drawBitmap(getAvatar(avatarSize.toInt()), avatarPaddingLeft, avatarPaddingTop, paint)
        }

        //画下半部分
        canvas.withSave {
            canvas.translate(avatarPaddingLeft + avatarSize/2, avatarPaddingTop + avatarSize/2)
            canvas.rotate(-degrees)
            camera.save()
            camera.rotateX(bottomRotateX)
            camera.applyToCanvas(canvas)
            camera.restore()
            canvas.clipRect(-avatarSize,0f,avatarSize,avatarSize)
            //斜角度切，就是旋转画布
            canvas.rotate(degrees)
            canvas.translate(-(avatarPaddingLeft + avatarSize/2), -(avatarPaddingTop + avatarSize/2))
            canvas.drawBitmap(getAvatar(avatarSize.toInt()), avatarPaddingLeft, avatarPaddingTop, paint)
        }

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