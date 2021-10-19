package com.example.myhencoderdemo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.myhencoderdemo.dp
import com.example.myhencoderdemo.getAvatar

/**
 * 以多只手指合力移动图片
 */
class MultiTouchView2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val bitmap = getAvatar(200.dp.toInt())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var originalOffsetX = 0f
    private var originalOffsetY = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    // 手指按下的位置
    private var downX = 0f
    private var downY = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap,offsetX,offsetY,paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 当前多个手指合在一起时，x 坐标
        var currentDownCenterX = 0f
        // 当前多个手指合在一起时，y 坐标
        var currentDownCenterY = 0f
        // 手指数
        var pointerCount = event.pointerCount
        // 所有手指按下的x坐标和
        var sumX = 0f
        // 所有手指按下的y坐标和
        var sumY = 0f
        val pointerUp = event.actionMasked == MotionEvent.ACTION_POINTER_UP
        for (i in 0 until pointerCount){
            if (!(pointerUp && i == event.actionIndex)){
                sumX += event.getX(i)
                sumY += event.getY(i)
            }
        }
        if (pointerUp){
            pointerCount -= 1
        }
        currentDownCenterX = sumX / pointerCount
        currentDownCenterY = sumY / pointerCount

        when(event.actionMasked){
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN,MotionEvent.ACTION_POINTER_UP->{
                downX = currentDownCenterX
                downY = currentDownCenterY
                originalOffsetX = offsetX
                originalOffsetY = offsetY
            }
            MotionEvent.ACTION_MOVE ->{
                var tempOffsetX = currentDownCenterX - downX + originalOffsetX
                var tempOffsetY = currentDownCenterY - downY + originalOffsetY
                // 限制图片的显示位置，不能移出屏幕外
                if (tempOffsetX < 0){
                    tempOffsetX = 0f
                }
                if (tempOffsetX + bitmap.width > width){
                    tempOffsetX = width.toFloat() - bitmap.width
                }
                if (tempOffsetY < 0){
                    tempOffsetY = 0f
                }
                if (tempOffsetY + bitmap.height > height){
                    tempOffsetY = height.toFloat() - bitmap.height
                }

                offsetX = tempOffsetX
                offsetY = tempOffsetY
                invalidate()
            }
        }
        return true
    }

}