package com.tomer.quotly.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.tomer.quotly.R
import com.tomer.quotly.utils.Particle
import kotlin.random.Random

class BlurCardView : View {
    //region CONSTRUCTOR

    constructor(con: Context) : super(con)

    constructor(con: Context, attributeSet: AttributeSet) : super(con, attributeSet)

    constructor(context: Context, attr: AttributeSet, defStyle: Int) : super(context, attr, defStyle)


//endregion CONSTRUCTOR

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

//region GLOBALS-->>>


    private val colRed = Color.RED
    private val colGreen = ContextCompat.getColor(context, R.color.green)

    private val pBlur = Paint().apply {
        color = colRed
        isAntiAlias = true
        isDither = true
        maskFilter = BlurMaskFilter(42.toPx(), BlurMaskFilter.Blur.NORMAL)
    }

    private val pStrokeBorder = Paint().apply {
        color = Color.CYAN
        isAntiAlias = true
        isDither = true
        strokeWidth = 4.toPx()
        style = Paint.Style.STROKE
    }

    private val pTrans = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        isDither = true
        alpha = 40
    }


    private val particles = mutableListOf<Particle>()
    private var isAnim = false

    private val quoteDrawable = (ContextCompat.getDrawable(context,R.drawable.ic_quote_double) as VectorDrawable).apply {
        setBounds(32,0,392,360)
    }

//endregion GLOBALS-->>>


    override fun onDraw(canvas: Canvas) {

        pBlur.color = colGreen
        canvas.drawCircle(width * .68f, height.toFloat(), 56.toPx(), pBlur)

        pBlur.color = colRed
        canvas.drawCircle(width * .32f, height.toFloat(), 56.toPx(), pBlur)

        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 32.toPx(), 32.toPx(), pTrans)
        canvas.drawRoundRect(4f, 4f, width - 4f, height - 4f, 32.toPx(), 32.toPx(), pStrokeBorder)
        if (isAnim)
            for (i in particles) i.draw(canvas)

        quoteDrawable.draw(canvas)
    }

    private fun Int.toPx(): Float = context.resources.displayMetrics.density * this

    fun setColor(col: Int) {
        ValueAnimator.ofArgb(pStrokeBorder.color, col).apply {
            addUpdateListener {
                pStrokeBorder.color = it.animatedValue as Int
                postInvalidate()
            }
            duration = 400
            start()
        }
    }


    fun animateFavParticles(isFav: Boolean) {
        val paint = Paint(pBlur).apply {
            maskFilter = BlurMaskFilter(12.toPx(), BlurMaskFilter.Blur.NORMAL)
            color = if (isFav) Color.RED else colGreen
        }

        for (i in 1..Random.nextInt(10, 20))
            particles.add(
                Particle(
                    paint,
                    if (isFav) PointF(width * .32f, height - 52.toPx())
                    else PointF(width * .68f,height - 52.toPx())
                )
            )

        isAnim = true
        ValueAnimator.ofInt(0, 1000).apply {
            addUpdateListener {
                postInvalidate()
            }
            doOnEnd {
                isAnim = false
                particles.clear()
                System.gc()
            }
            duration = 2500
            start()
        }
    }

}