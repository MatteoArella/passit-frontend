package com.github.passit.ui.view

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import com.github.passit.R

class LaunchScreenView(context: Context) : View(context) {

    private val radius = 1000f
    private val paint = Paint().apply {
        isDither = true
        shader = RadialGradient(0f, 0f, radius,
            ContextCompat.getColor(context, R.color.colorPrimary),
            ContextCompat.getColor(context, R.color.colorPrimaryDark),
            Shader.TileMode.CLAMP
        )
    }
    private val m = Matrix()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        m.setTranslate(width / 2f, height / 2f)
        paint.shader.setLocalMatrix(m)
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

    }
}