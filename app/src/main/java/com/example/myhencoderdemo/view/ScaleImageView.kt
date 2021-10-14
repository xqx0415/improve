package com.example.myhencoderdemo.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
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
    private val bigFactor = 1.8f

    //是否为放大状态
    private var isBig = false

    //当前放缩比，作为属性动画
    private var currentScale = 0f
        set(value) {
            field = value
            invalidate()
        }

    //放缩属性动画
    private val scaleAnimator =
        ObjectAnimator.ofFloat(this@ScaleImageView, "currentScale", smallScale, bigScale)

    private val gestureDetectorCompat = GestureDetectorCompat(context, XieGestureDetector())
    private val overScroller = OverScroller(context)
    private val flingRunner = FlingRunner()

    private val scaleGestureDetector = ScaleGestureDetector(context, XieScaleDetector())

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
        currentScale = smallScale
        scaleAnimator.setFloatValues(smallScale, bigScale)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //滑动大图需要做的偏移
        val localScale = (currentScale - smallScale) / (bigScale - smallScale)
        canvas.translate(offsetX * localScale, offsetY * localScale)//3
        canvas.scale(currentScale, currentScale, width / 2f, height / 2f)//2
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)//1
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //手势代理
        scaleGestureDetector.onTouchEvent(event)
        if (!scaleGestureDetector.isInProgress) {
            return gestureDetectorCompat.onTouchEvent(event)
        }
        return true
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
                //如果条件成立，表示已经进行了双指缩放，那么双击的时候，就要从已经缩放的大小恢复到最小
                if (currentScale != smallScale) {
                    val tempAnimator = scaleAnimator.clone()
                    tempAnimator.setFloatValues(smallScale,currentScale)
                    tempAnimator.reverse()
                    return true
                }
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
            if (isBig) {
                overScroller.fling(
                    offsetX.toInt(),
                    offsetY.toInt(),
                    velocityX.toInt(),
                    velocityY.toInt(),
                    (-(bitmap.width * bigScale - width) / 2).toInt(),
                    ((bitmap.width * bigScale - width) / 2).toInt(),
                    (-(bitmap.height * bigScale - height) / 2).toInt(),
                    ((bitmap.height * bigScale - height) / 2).toInt()
                )
                ViewCompat.postOnAnimation(this@ScaleImageView, flingRunner)
            }
            return true
        }
    }

    //修正偏移，不能超过图片的上下左右两边
    private fun fixOffset() {
        offsetX = offsetX.coerceAtLeast(-(bitmap.width * bigScale - width) / 2)
            .coerceAtMost((bitmap.width * bigScale - width) / 2)
        offsetY = offsetY.coerceAtLeast(-(bitmap.height * bigScale - height) / 2)
            .coerceAtMost((bitmap.height * bigScale - height) / 2)
    }

    //快滑响应
    inner class FlingRunner : Runnable {
        override fun run() {
            if (overScroller.computeScrollOffset()) {
                offsetX = overScroller.currX.toFloat()
                offsetY = overScroller.currY.toFloat()
                invalidate()
                ViewCompat.postOnAnimation(this@ScaleImageView, this)
            }
        }
    }

    inner class XieScaleDetector : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            val tempScale = currentScale * scaleGestureDetector.scaleFactor
            //如果条件成立，就不消费上次的放缩比，使得不管手指放大或者缩小多少，都要重新恢复到临界值才能重新进行缩放
            if (tempScale < smallScale || tempScale > bigScale) {
                return false
            } else {
                currentScale *= scaleGestureDetector.scaleFactor
            }
//            isBig = currentScale == bigScale
            //返回值是true， scaleGestureDetector.scaleFactor 表示 是上次的放缩比跟此次放缩比的值
            //false， 表示 初始值和当前的
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            if (!isBig) {
                offsetX = (width / 2 - detector.focusX) * bigScale
                offsetY = (height / 2 - detector.focusY) * bigScale
                fixOffset()
            }
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            isBig = bitmap.width * currentScale > width || bitmap.height * currentScale > height
        }

    }

}