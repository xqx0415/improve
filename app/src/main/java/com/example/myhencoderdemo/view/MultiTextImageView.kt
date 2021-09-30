package com.example.myhencoderdemo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.myhencoderdemo.R
import com.example.myhencoderdemo.dp

const val TAG = "MultiTextImageView"

class MultiTextImageView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    //    private val text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
    private val text =
        "我正在通过python中的Element Tree解析XML文件，并将内容写入CPP文件。子标签的内容将因不同的标签而异。例如，第一个事件标签有派对标记作为子标记，而第二个事件标记没有在解析之前，如何检查标记是否存在？在获取属性的值之前，如何检查属性是否存在。我要检查标签，然后取它们的值。您也可以使用标准Python技术显式地测试属性。正规方法是用载入第三方xml库（如lxml）是用xpath查找。XML指可扩展标记语言（Extensible Markup Language）。XML被设计用于结构化、存储和传输数据。XML是一种标记语言，很类似于HTML。XML没有像HTML那样具有预定义标签，需要程序员自定义标签。XML被设计为具有自我描述性，并且是W3C的标准"

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 18.dp
        style = Paint.Style.FILL
        //设置字体
        typeface = ResourcesCompat.getFont(context,R.font.gyjx_font)
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //图片大小，为正方形图片
    private val avatarSize = 150.dp
    //图片距离顶部的距离
    private val avatarTopPadding = 55.dp
    //用户获取文字的刻画范围
    private val bounds = Rect()

    //绘制文字的起始位置
    private var start = 0
    //每次需要绘制文字时，根据可用宽度获取到的可绘制文字个数
    private var count = 0
    //绘制文字实际在View上的绘制宽度，每次计算可绘制文字个数时都会更新为最新值，如果不需要改参数对应的位置可传null
    private val measuredWidth = floatArrayOf(0f);
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画图
        canvas.drawBitmap(
            getAvatar(avatarSize.toInt()),
            width - avatarSize,
            avatarTopPadding,
            paint
        )
        textPaint.getTextBounds(text, 0, text.length, bounds)
        var verticalHeight = -bounds.top.toFloat()
        var maxMeasuredWidth = 0f
        while (start < text.length) {
            //如果文字的底部的位置 > 图片距离顶部的距离  或者 文字的顶部 < 图片距离顶部的距离+图片的高度。文字显示最大宽度就是 View的宽-图片宽度
            // 反之，就是View的宽度
            maxMeasuredWidth =
                if (verticalHeight > avatarTopPadding && (verticalHeight - bounds.height()) < (avatarTopPadding + avatarSize)) {
                    width - avatarSize
                } else {
                    width.toFloat()
                }
            //根据 文字可以显示的最大宽度 ，计算出有多少个文字
            count =
                textPaint.breakText(text, start, text.length, true, maxMeasuredWidth, measuredWidth)
//            Log.i(TAG, "onDraw: start = $start, count = $count, (start+count)=${start+count}")
            //通过计算出的文字个数，画出对应的文字
            canvas.drawText(text, start, start + count, 0f, verticalHeight, textPaint)
            //将文字绘制的高度更新
            verticalHeight += textPaint.fontSpacing
            //更新下一次文字绘制的起始位置
            start += count
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