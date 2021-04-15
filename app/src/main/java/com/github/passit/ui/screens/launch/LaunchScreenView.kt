package com.github.passit.ui.screens.launch

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.passit.R
import com.github.passit.core.platform.TiltListener
import com.github.passit.core.platform.TiltSensor

class LaunchScreenView(context: Context) : View(context), TiltListener {
    private val tiltSensor = TiltSensor(context)
    private val backgroundPaint = Paint().apply {
        isDither = true
    }
    private val textPaint = Paint().apply {
        textSize = 82f * resources.displayMetrics.density
        isAntiAlias = true
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.poppins_bold), Typeface.DEFAULT_BOLD.style)
    }
    private var center = PointF(0f, 0f)
    private val gradientMatrix = Matrix()
    private val textBounds = Rect()
    private val logoText = "PassIt!"

    private lateinit var bookSizeAnimator: ValueAnimator
    private var bookSizeMultiplier = 2.5f
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bookSizeAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            addUpdateListener {
                bookSizeMultiplier = Math.abs(Math.sin((it.animatedValue as Float) * 2 * Math.PI).toFloat()) + 2.5f
            }
            duration = 3000L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        bookSizeAnimator.start()
        tiltSensor.addOnTiltListener(this)
        tiltSensor.register()
    }

    override fun onDetachedFromWindow() {
        bookSizeAnimator.cancel()
        tiltSensor.unregister()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        center.set(w / 2f, h / 2f)
        // Create gradient after getting sizing information
        backgroundPaint.shader = RadialGradient(center.x, center.y, w / 2f,
                ContextCompat.getColor(context, R.color.colorPrimaryLight),
                ContextCompat.getColor(context, R.color.colorPrimary),
                Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // draw background
        canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // draw logo
        val b = getBitmap(context, R.drawable.ic_logo)!!
        val logo = getResizedBitmap(b, (b.width * bookSizeMultiplier).toInt(), (b.height * bookSizeMultiplier).toInt())
        canvas.drawBitmap(logo, (width - logo.width) / 2.0f, (height - logo.height) / 2.0f, null)

        // draw logo text
        textPaint.getTextBounds(logoText, 0, logoText.length, textBounds)
        canvas.drawText(logoText, (width - textBounds.width()) / 2.0f, height.toFloat(), textPaint)
    }

    override fun onTilt(pitchRollRad: Pair<Double, Double>) {
        val pitchRad = pitchRollRad.first
        val rollRad = pitchRollRad.second

        val maxYOffset = center.y.toDouble()
        val maxXOffset = center.x.toDouble()

        val yOffset = -(Math.sin(pitchRad) * maxYOffset)
        val xOffset = (Math.sin(rollRad) * maxXOffset)

        updateGradient(xOffset.toFloat() + center.x, yOffset.toFloat() + center.y)
    }

    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, bitmap.width, bitmap.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    private fun getBitmap(context: Context, drawableId: Int): Bitmap {
        return when (val drawable = ContextCompat.getDrawable(context, drawableId)) {
            is BitmapDrawable -> BitmapFactory.decodeResource(context.resources, drawableId)
            is VectorDrawable -> getBitmap(drawable)
            else -> throw IllegalArgumentException("unsupported drawable type")
        }
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    private fun updateGradient(x: Float, y: Float) {
        gradientMatrix.setTranslate(x - center.x, y - center.y)
        backgroundPaint.shader.setLocalMatrix(gradientMatrix)
        postInvalidateOnAnimation()
    }
}