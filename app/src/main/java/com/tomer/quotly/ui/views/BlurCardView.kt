package com.tomer.quotly.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.tomer.quotly.R

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


    private val colRed = ContextCompat.getColor(context, R.color.primary_light)
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


//endregion GLOBALS-->>>


    override fun onDraw(canvas: Canvas) {

        pBlur.color = colGreen
        canvas.drawCircle(width * .68f, height.toFloat(), 56.toPx(), pBlur)

        pBlur.color = colRed
        canvas.drawCircle(width * .32f, height.toFloat(), 56.toPx(), pBlur)

        canvas.drawRoundRect(4f, 4f, width - 4f, height - 4f, 32.toPx(), 32.toPx(), pStrokeBorder)
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

}