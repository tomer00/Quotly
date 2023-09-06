package com.tomer.quotly.utils

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import androidx.core.animation.doOnEnd
import kotlin.random.Random

class Particle(
    private val paint: Paint,
    private val startPos: PointF
) {

    private val endPos = PointF(0f, 0f)

    private var radiStart = 0f
    private var radiEnd = 0f

    private var animated = 0f
    private var radiusAnimated = 0f

    init {

        endPos.set(Random.nextInt(-200, 200) + startPos.x, startPos.y - Random.nextInt(120, 360).toFloat())

        radiEnd = (32 * Random.nextFloat())

        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                animated = it.animatedValue as Float
                radiusAnimated = it.animatedValue as Float
            }
            doOnEnd {
                ValueAnimator.ofFloat(1f, 0f).apply {
                    addUpdateListener {
                        radiusAnimated = it.animatedValue as Float
                    }
                    duration = 120
                    startDelay = Random.nextLong(100)
                    start()
                }
            }
            duration = Random.nextLong(400, 1500)
            startDelay = Random.nextLong(1400)
            start()
        }

    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(
            startPos.x + (endPos.x - startPos.x) * animated,
            startPos.y + (endPos.y - startPos.y) * animated,
            radiStart + (radiEnd - radiStart) * radiusAnimated,
            paint
        )
    }

}