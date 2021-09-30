package com.example.myhencoderdemo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.myhencoderdemo.dp
import kotlin.math.cos
import kotlin.math.sin

//开口角度
const val OPEN_ANGLE = 120

//刻度的宽高
const val DASH_WIDTH = 5
const val DASH_HEIGHT = 10

//刻度尺中指针的长度
const val POINTER_LENGTH = 180

class RulerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2.dp
        style = Paint.Style.STROKE
        color = Color.parseColor("#00aacc")
    }

    //弧
    private val path = Path()

    //刻度
    private val dashPath = Path()

    //用于画刻度的类
    private lateinit var pathDashPathEffect: PathDashPathEffect

    //弧的半径
    private val pathRadius = 100.dp

    init {
        dashPath.addRect(0f, 0f, DASH_WIDTH.toFloat(), DASH_HEIGHT.toFloat(), Path.Direction.CCW)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //设置弧
        path.reset()
        path.addArc(
            w / 2f - pathRadius,
            h / 2 - pathRadius,
            w / 2f + pathRadius,
            h / 2f + pathRadius,
            90f + OPEN_ANGLE / 2f,
            360f - OPEN_ANGLE
        )
        //用于测量弧长度的类
        val pathMeasure = PathMeasure(path, false)
        //用于画刻度的 PathEffect， 以dashPath为图形，以 (弧长度-刻度宽度)/20[刻度尺数量] 为间隔 ，从0开始。
        //当Paint设置了这个PathEffect之后。就会在canvas画图形的时候，画出对应规则的图形
        pathDashPathEffect = PathDashPathEffect(
            dashPath,
            (pathMeasure.length - DASH_WIDTH) / 20,
            0f,
            PathDashPathEffect.Style.MORPH
        )
    }

    private var mark: Int = 0
    fun setMark(mark: Int) {
        if (mark in 0..20) {
            if (mark == this.mark)return
            this.mark = mark
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画弧
        canvas.drawPath(path, paint)
        paint.pathEffect = pathDashPathEffect
        //画刻度
        canvas.drawPath(path, paint)
        paint.pathEffect = null
        //画指向刻度的直线
        canvas.drawLine(
            width / 2f, height / 2f,
            (width / 2f + POINTER_LENGTH * cos(markToRadians(mark))).toFloat(),
            (height / 2f + POINTER_LENGTH * sin(markToRadians(mark))).toFloat(),
            paint
        )
    }

    //根据给出的指针位置，算出对应的角度值Radians并返回
    private fun markToRadians(mark: Int): Double {
        return Math.toRadians(((90f + OPEN_ANGLE / 2f) + mark * (360f - OPEN_ANGLE) / 20).toDouble())
    }
}