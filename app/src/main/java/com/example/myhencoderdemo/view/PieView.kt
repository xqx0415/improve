package com.example.myhencoderdemo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.myhencoderdemo.dp
import kotlin.math.cos
import kotlin.math.sin

class PieView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val colors = intArrayOf(
        Color.parseColor("#00FF40"),
        Color.parseColor("#0080FF"),
        Color.parseColor("#FF80FF"),
        Color.parseColor("#008040"),
        Color.parseColor("#8000FF"),
    )

    private val angles = floatArrayOf(35f, 45f, 80f, 70f, 130f)

    private val radius = 120.dp
    private val selectOffsetLength = 30

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2.dp
        style = Paint.Style.FILL
    }

    private var selectIndex = 0;
    fun setSelectIndex(index:Int){
        if (index in 0..4){
            if (selectIndex != index){
                selectIndex = index
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        val selectIndex = 2
        var startAngle = 0f
        for ((index, angle) in angles.withIndex()) {
            paint.color = colors[index]

            //选择的扇形块偏移出去
            if (index == selectIndex) {
                canvas.save()
                canvas.translate(
                    (selectOffsetLength * cos(Math.toRadians((startAngle + angle/2).toDouble()))).toFloat(),
                    (selectOffsetLength * sin(Math.toRadians((startAngle + angle/2).toDouble()))).toFloat()
                )
            }
            canvas.drawArc(
                width / 2f - radius,
                height / 2f - radius,
                width / 2f + radius,
                height / 2f + radius,
                startAngle,
                angle,
                true,
                paint
            )
            //起始角度要添加已画的角度
            startAngle += angle
            //偏移的画布重新恢复
            if (index == selectIndex) {
                canvas.restore()
            }
        }

    }

}