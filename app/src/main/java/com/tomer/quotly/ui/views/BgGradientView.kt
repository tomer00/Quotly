package com.tomer.quotly.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.tomer.quotly.utils.BgBalls
import kotlin.concurrent.thread
import kotlin.random.Random

class BgGradientView : View {

    //region CONSTRUCTOR

    constructor(con: Context) : super(con)

    constructor(con: Context, attributeSet: AttributeSet) : super(con, attributeSet)

    constructor(context: Context, attr: AttributeSet, defStyle: Int) : super(context, attr, defStyle)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        BgBalls.height = h
        BgBalls.width = w

        for (i in 1..Random.nextInt(4, 8))
            balls.add(BgBalls(pBlur))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator.cancel()
        balls.forEach {
            it.stop()
        }
    }

    //endregion CONSTRUCTOR


    //region GLOBALS-->>>


    private val colorBalls = Color.CYAN
    private val pBlur = Paint().apply {
        color = colorBalls
        maskFilter = BlurMaskFilter(120f, BlurMaskFilter.Blur.NORMAL)
        isAntiAlias = true
    }
    private val valueAnimator = ValueAnimator.ofInt(0, 1000).apply {
        this.addUpdateListener {
            postInvalidate()
        }
        duration = 1000
        repeatCount = -1
        start()
    }

    private val balls = mutableListOf<BgBalls>()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    //endregion GLOBALS-->>>


    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(100f, 500f, 140f, pBlur)
        balls.forEach {
            it.draw(canvas)
        }
    }


    fun setColor(col: Int) {
        ValueAnimator.ofArgb(pBlur.color, col).apply {
            addUpdateListener {
                pBlur.color = it.animatedValue as Int
            }
            duration = 400
            start()
        }
    }


}