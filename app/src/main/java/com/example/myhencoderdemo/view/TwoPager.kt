package com.example.myhencoderdemo.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.OverScroller
import androidx.core.view.children
import kotlin.math.abs

class TwoPager(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    // 速度追踪
    private val velocityTracker = VelocityTracker.obtain()

    private val overScroller = OverScroller(context)

    // 用该类来获取滑动距离阈值，最大速度，最小速度阈值
    private val viewConfiguration = ViewConfiguration.get(context)
    private val pagingSlop = viewConfiguration.scaledTouchSlop
    private val minVelocity = viewConfiguration.scaledMinimumFlingVelocity
    private val maxVelocity = viewConfiguration.scaledMaximumFlingVelocity

    private var downX = 0f
    private var downY = 0f
    private var downScrollX = 0f

    private var scrolling = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 按照 父View 的规则测量 子View
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft = 0
        var childRight = width
        for (child in children) {
            child.layout(childLeft, 0, childRight,height)
            childLeft += width
            childRight += width
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        velocityTracker.addMovement(event)
        var result = false
        println("event.actionMasked = ${event.actionMasked}")
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrolling = false
                downX = event.x
                downY = event.y
                downScrollX = scrollX.toFloat()
            }
            MotionEvent.ACTION_MOVE -> if (!scrolling) {
                val dx = downX - event.x
                if (abs(dx) > pagingSlop) {
                    scrolling = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    result = true
                }
            }
        }
        return result
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            // 每次按下屏幕时，先清空速度追踪器
            velocityTracker.clear()
        }
        // 将所有触摸的事件传给速度追踪器
        velocityTracker.addMovement(event)

        when(event.actionMasked){
            MotionEvent.ACTION_DOWN ->{
                downX = event.x
                downY = event.y
                downScrollX = scrollX.toFloat()
            }
            MotionEvent.ACTION_MOVE->{
                var distance = downX - event.x + downScrollX
                distance = distance.coerceAtLeast(0f).coerceAtMost(width.toFloat())
                scrollTo(distance.toInt(),0)
            }
            MotionEvent.ACTION_UP ->{
                //手指抬起时，计算当前滑动的距离有没有超过屏幕的一半
                velocityTracker.computeCurrentVelocity(1000, maxVelocity.toFloat())
                //速度为负，左滑，反之右滑
                val curVelocity = velocityTracker.xVelocity
                val curScrollX = scrollX
                // 剩余需要滑动的距离
                var distanceXValue = 0f
                // 如果速度小于最小速度，根据当前滑动距离是否小于屏幕的一半，返回最终的需要滑动的距离
                distanceXValue = if (abs(curVelocity) < minVelocity){
                    if (curScrollX > width/2){
                        (width - curScrollX).toFloat()
                    }else{
                        (-curScrollX).toFloat()
                    }
                }else{
                    if (curVelocity < 0){
                        (width - curScrollX).toFloat()
                    }else{
                        (-curScrollX).toFloat()
                    }
                }
                // 需要滚动的距离交给 overScroller 处理计算
                overScroller.startScroll(scrollX,0, distanceXValue.toInt(),0)
                // 调用该方法，会执行 computeScroll ，改方法在 draw 方法中会被调用
                postInvalidateOnAnimation()
            }
        }

        return true
    }

    override fun computeScroll() {
        // 当 overScroller 计算的偏移已完成时，返回false
        if (overScroller.computeScrollOffset()){
            scrollTo(overScroller.currX,overScroller.currY)
            postInvalidateOnAnimation()
        }
    }

}