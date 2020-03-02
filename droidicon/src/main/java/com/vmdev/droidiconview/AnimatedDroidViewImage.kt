package com.vmdev.droidiconview

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.graphics.ColorUtils

class AnimatedDroidViewImage(context: Context, attributeSet: AttributeSet? = null) :
    AnimatedDroidIconView(context, attributeSet) {

    var bitmap: Bitmap? = null
        set(value) {
            field = getRoundedCornerBitmap(value)
            postInvalidate()
        }

    protected var frameScale = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val bitmapTranform: Matrix = Matrix()

    private val framePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        color = ColorUtils.setAlphaComponent(strokeColor, ((this@AnimatedDroidViewImage.alpha * 255).toInt()))
        setShadowLayer(25f, 0f, 0f, Color.WHITE)
    }
    private val frameRect = RectF()

    private var touched = false

    private val frameAnimator: ObjectAnimator =
        ObjectAnimator.ofFloat(this, "frameScale", 0f, 0.005f)

    init {
        with(frameAnimator) {
            duration = 600
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        framePaint.strokeWidth = rectSize * STROKE_WIDTH_FACTOR
    }

    override fun onPreDrawBody(canvas: Canvas, rect: RectF) {

    }

    override fun onPostDrawBody(canvas: Canvas, rect: RectF) {
        canvas.save()

        bitmap?.let {
            bitmapTranform.reset()
            val scaleBitmap = calculateBitmapTransform(it, rect, 0.5f)
            val scaleFrame = calculateBitmapTransform(it, rect, 0.75f)
            bitmapTranform.postScale(scaleBitmap, scaleBitmap)
            bitmapTranform.postTranslate(
                width * 0.5f - it.width * scaleBitmap * 0.5f,
                halfHeightWithShift - it.height * scaleBitmap * 0.5f
            )
            canvas.drawBitmap(it, bitmapTranform, null)

            frameRect.left = width * 0.5f - it.width * scaleFrame * 0.5f - width * frameScale
            frameRect.right = width * 0.5f + it.width * scaleFrame * 0.5f + width * frameScale
            frameRect.top = halfHeightWithShift - it.height * scaleFrame * 0.5f - height * frameScale
            frameRect.bottom = halfHeightWithShift + it.height * scaleFrame * 0.5f + height * frameScale
        }

        canvas.restore()

        val corners = 16f
        canvas.drawRoundRect(frameRect, corners, corners, framePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touched = frameRect.contains(event.x, event.y)
                return touched
            }
            MotionEvent.ACTION_UP -> {
                if (touched && frameRect.contains(event.x, frameRect.top)) {
                    touched = false
                    performClick()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun startFrameAnimation() {
        frameAnimator.start()
    }

    fun stopFrameAnimation() {
        frameAnimator.pause()
        frameAnimator.end()
    }

    private fun calculateBitmapTransform(it: Bitmap, rect: RectF, factor: Float): Float {
        val biggerSize = if (it.width > it.height) it.width else it.height
        return rect.width() / biggerSize * factor
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap?): Bitmap? {
        bitmap?.let {
            val pixels = bitmap.width * 0.07f
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
        return bitmap
    }
}