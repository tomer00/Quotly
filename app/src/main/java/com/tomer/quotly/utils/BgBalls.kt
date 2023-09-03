package com.tomer.quotly.utils

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import androidx.core.animation.doOnRepeat
import kotlin.random.Random

class BgBalls(
    private val paint: Paint
) {


    companion object {
        var width = 0
        var height = 0
    }

    //region GLOBALS--->>>

    private val pointStart = Point(0, 0)
    private val pointEnd = Point(0, 0)

    private var radiusStart = 0f
    private var radiusEnd = 0f

    private var aniMated = 0f

    init {
        pointStart.set(Random.nextInt(width), Random.nextInt(height))
        pointEnd.set(Random.nextInt(width), Random.nextInt(height))

        radiusStart = 20f + (180f - 20f) * Random.nextFloat()
        radiusEnd = 20f + (180f - 20f) * Random.nextFloat()
    }

    private val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        this.addUpdateListener {
            aniMated = it.animatedValue as Float
        }
        doOnRepeat {
            pointStart.set(pointEnd.x, pointEnd.y)
            pointEnd.set(Random.nextInt(width), Random.nextInt(height))

            radiusStart = radiusEnd
            radiusEnd = 20f + (180f - 20f) * Random.nextFloat()
        }
        duration = Random.nextLong(5000, 60000)
        repeatCount = -1
        startDelay = Random.nextLong(1000)
        start()
    }

    //endregion GLOBALS--->>>

    fun draw(canvas: Canvas) {
        canvas.drawCircle(
            pointStart.x + (pointEnd.x - pointStart.x) * aniMated,
            pointStart.y + (pointEnd.y - pointStart.y) * aniMated,
            radiusStart + (radiusEnd - radiusStart) * aniMated,
            paint
        )
    }


    fun stop() {
        valueAnimator.cancel()
    }
}