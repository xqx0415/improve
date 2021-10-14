package com.example.myhencoderdemo.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import com.example.myhencoderdemo.dp
import com.example.myhencoderdemo.getAvatar
import kotlin.math.max
import kotlin.math.min

class ScaleImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val imageSize = 280.dp

    private val bitmap = getAvatar(imageSize.toInt())

    private var offsetX = 0f
    private var offsetY = 0f
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f

    private var smallScale = 0f
    private var bigScale = 0f
    private val bigFactor = 2.8f

    private var isBig = false

    private var scaleFraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val scaleAnimator by lazy {
        ObjectAnimator.ofFloat(this@ScaleImageView, "scaleFraction", 0f, 1f)
    }

    private val gestureDetectorCompat = GestureDetectorCompat(context, XieGestureDetector())
    private val overScroller = OverScroller(context)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        originalOffsetX = (width - imageSize) / 2
        originalOffsetY = (height - imageSize) / 2

        //图片的宽高比，宽\View宽 > 高\View高   偏宽的图片，反之则偏长的图片
        if (width / bitmap.width > height / bitmap.height) {
            smallScale = height / bitmap.height.toFloat()
            bigScale = width / bitmap.width * bigFactor
        } else {
            smallScale = width / bitmap.width.toFloat()
            bigScale = height / bitmap.height * bigFactor
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(offsetX * scaleFraction, offsetY * scaleFraction)
        val scale = smallScale + (bigScale - smallScale) * scaleFraction
        canvas.scale(scale, scale, width / 2f, height / 2f)
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetectorCompat.onTouchEvent(event)
    }

    inner class XieGestureDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            isBig = !isBig
            if (isBig) {
                //双击后，以点击的点为中心放大
                offsetX = (width / 2 - e.x) * bigScale
                offsetY = (height / 2 - e.y) * bigScale
//                offsetX = (e.x - (width / 2)) * (1-bigScale/smallScale)
//                offsetY = (e.y - height / 2 ) * (1-bigScale/smallScale)
                fixOffset()
                scaleAnimator.start()
            } else {
                scaleAnimator.reverse()
            }
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            //放大的情况下，可滑动图片
            if (isBig) {
                //distanceX distanceY 是起始位置-终点位置，所以要 -=
                offsetX -= distanceX
                offsetY -= distanceY
                fixOffset()
                invalidate()
            }
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            //放大图片情况下，
            if (isBig){
                overScroller.fling(
                    offsetX.toInt(),
                    offsetY.toInt(),
                    velocityX.toInt(),
                    velocityY.toInt(),
                    (-(bitmap.width * bigScale - width) / 2).toInt(),
                    ((bitmap.width * bigScale - width) / 2).toInt(),
                    (-(bitmap.height * bigScale - height) / 2).toInt(),
                    ((bitmap.height * bigScale - height) / 2).toInt(),
                    40.dp.toInt(), 40.dp.toInt()
                )
                ViewCompat.postOnAnimation(this@ScaleImageView,FlingRunner())
            }
            return true
        }
    }

    private fun fixOffset() {
        offsetX = min(offsetX, (bitmap.width * bigScale - width) / 2)
        offsetX = max(offsetX, -(bitmap.width * bigScale - width) / 2)
        offsetY = min(offsetY, (bitmap.height * bigScale - height) / 2)
        offsetY = max(offsetY, -(bitmap.height * bigScale - height) / 2)
    }

    inner class FlingRunner : Runnable{
        override fun run() {
            if (overScroller.computeScrollOffset()){
                offsetX = overScroller.currX.toFloat()
                offsetY = overScroller.currY.toFloat()
                invalidate()
                ViewCompat.postOnAnimation(this@ScaleImageView,this)
            }
        }

    }
}