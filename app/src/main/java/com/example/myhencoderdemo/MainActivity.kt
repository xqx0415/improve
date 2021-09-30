package com.example.myhencoderdemo

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myhencoderdemo.view.CameraView
import com.example.myhencoderdemo.view.RulerView

class MainActivity : AppCompatActivity() {
    /*lateinit var rulerView: RulerView
    lateinit var start:Button
    var mark = 0*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*rulerView = findViewById<RulerView>(R.id.id_ruler)
        start = findViewById<Button>(R.id.id_button)

        start.setOnClickListener {
            mark++
            if (mark == 21){
                mark = 0
            }
            rulerView.setMark(mark)
        }*/

        //使用属性动画，循环翻转View
        /*val view = findViewById<CameraView>(R.id.id_ruler)
        val objectAnimator = ObjectAnimator.ofFloat(view,"degrees",0f,360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.RESTART
        objectAnimator.repeatCount = ValueAnimator.INFINITE
        objectAnimator.start()*/

    }

}