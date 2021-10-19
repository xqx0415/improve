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
 * 哪只手指最后按下屏幕，即获取移动焦点
 */
class MultiTouchView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val bitmap = getAvatar(200.dp.toInt())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var offsetX = 0f
    private var offsetY = 0f
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f
    // 手指按下的位置
    private var downX = 0f
    private var downY = 0f

    // 当前的跟踪的手指id
    var currentPointerId = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap,offsetX,offsetY,paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked){
            MotionEvent.ACTION_DOWN ->{
                currentPointerId = event.getPointerId(0)
                downX = event.x
                downY = event.y
                originalOffsetX = offsetX
                originalOffsetY = offsetY
            }
            MotionEvent.ACTION_POINTER_DOWN ->{
                val actionIndex = event.actionIndex
                currentPointerId = event.getPointerId(actionIndex)
                downX = event.getX(actionIndex)
                downY = event.getY(actionIndex)
                originalOffsetX = offsetX
                originalOffsetY = offsetY
            }
            MotionEvent.ACTION_MOVE ->{
                val currentIndex = event.findPointerIndex(currentPointerId)
                var tempOffsetX = event.getX(currentIndex) - downX + originalOffsetX
                var tempOffsetY = event.getY(currentIndex) - downY + originalOffsetY
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
            MotionEvent.ACTION_POINTER_UP ->{
                val actionIndex = event.actionIndex
                // 如果当前抬起的手指是最后一个，那就获取再前一个的手指对应的id
                val upIndex = if (actionIndex == event.pointerCount -1){
                    event.pointerCount - 2
                }else{
                    event.pointerCount - 1
                }
                currentPointerId = event.getPointerId(upIndex)
                // 将按下的坐标重新更新为最新这根可以操作的手指上，避免换手指时会有跳动的情况出现
                downX = event.getX(upIndex)
                downY = event.getY(upIndex)
                originalOffsetX = offsetX
                originalOffsetY = offsetY
            }
        }
        return true
    }

}