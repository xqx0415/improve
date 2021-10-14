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

    //图片滑动偏移
    private var offsetX = 0f
    private var offsetY = 0f
    //图片原始偏移
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f

    //小图放缩比
    private var smallScale = 0f
    //大图放缩比
    private var bigScale = 0f
    //大图系数
    private val bigFactor = 2.8f

    //是否为放大状态
    private var isBig = false

    //放缩系数，作为属性动画
    private var scaleFraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    //放缩属性动画
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
        //滑动大图需要做的偏移
        canvas.translate(offsetX * scaleFraction, offsetY * scaleFraction)//3
        val scale = smallScale + (bigScale - smallScale) * scaleFraction
        canvas.scale(scale, scale, width / 2f, height / 2f)//2
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)//1
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //手势代理
        return gestureDetectorCompat.onTouchEvent(event)
    }

    inner class XieGestureDetector : GestureDetector.SimpleOnGestureListener() {
        //始终返回true，因为要拦截事件
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            //双击，图片放大或缩小
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
            //放大图片情况下，计算快滑
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

    //修正偏移，不能超过图片的上下左右两边
    private fun fixOffset() {
        offsetX = min(offsetX, (bitmap.width * bigScale - width) / 2)
        offsetX = max(offsetX, -(bitmap.width * bigScale - width) / 2)
        offsetY = min(offsetY, (bitmap.height * bigScale - height) / 2)
        offsetY = max(offsetY, -(bitmap.height * bigScale - height) / 2)
    }

    //快滑响应
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