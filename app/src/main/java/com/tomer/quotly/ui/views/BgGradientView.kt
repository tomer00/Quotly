package com.tomer.quotly.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
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

        handler.post {
            for (i in 1..Random.nextInt(5, 10))
                balls.add(BgBalls(pBlur))
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        aniMat = false
        balls.forEach {
            it.stop()
        }
        thread.looper.quitSafely()
        thread.quitSafely()
    }

    //endregion CONSTRUCTOR


    //region GLOBALS-->>>
    private val thread = HandlerThread("BackgroundThread").apply {
        start()
    }
    private val handler = Handler(thread.looper)


    private val colorBalls = Color.CYAN
    private val pBlur = Paint().apply {
        color = colorBalls
        maskFilter = BlurMaskFilter(120f, BlurMaskFilter.Blur.NORMAL)
        isAntiAlias = true
    }

    private var aniMat = true
    private val frameDelay = 80L

    private val balls = mutableListOf<BgBalls>()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        thread {
            while (aniMat) {
                SystemClock.sleep(frameDelay)
                postInvalidate()
            }
        }
    }

    //endregion GLOBALS-->>>


    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(100f, 500f, 140f, pBlur)
        balls.forEach {
            it.draw(canvas)
        }
    }


    fun setColor(col: Int) {
        handler.post {
            ValueAnimator.ofArgb(pBlur.color, col).apply {
                addUpdateListener {
                    pBlur.color = it.animatedValue as Int
                    this@BgGradientView.post {
                        postInvalidate()
                    }
                }
                duration = 400
                start()
            }
        }
    }


}