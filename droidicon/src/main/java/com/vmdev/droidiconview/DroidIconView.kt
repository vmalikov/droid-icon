package com.vmdev.droidiconview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils

open class DroidIconView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    companion object {
        private const val STROKE_WIDTH = 8f
        internal const val STROKE_WIDTH_FACTOR: Float = 0.033f
    }

    protected var halfHeightWithShift: Float = 0f

    protected var rectSize: Int = 0
    private var bodyRect: RectF = RectF()
    private var leftLegRect: RectF = RectF()
    private var rightLegRect: RectF = RectF()
    private var leftHandRect: RectF = RectF()
    private var rightHandRect: RectF = RectF()
    private var headRect: RectF = RectF()
    private var leftEyePoint: PointF = PointF()
    private var rightEyePoint: PointF = PointF()
    private var eyeRadius: Float = 0f

    private var leftAntennaRect: RectF = RectF()
    private var rightAntennaRect: RectF = RectF()

    private val defaultBodyColor = Color.parseColor("#A6C645")
    private val defaultEyeColor = Color.WHITE
    private val defaultStrokeColor = Color.BLACK

    private var bodyColor: Int = defaultBodyColor
    private var eyesColor: Int = defaultEyeColor
    protected var strokeColor: Int = defaultStrokeColor

    private val bodyPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val bodyStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth =
            STROKE_WIDTH
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val eyePaint = Paint().apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.DroidIconView, 0, 0
        )
            .apply {
                try {
                    bodyColor = getColor(R.styleable.DroidIconView_body_color, defaultBodyColor)
                    eyesColor = getColor(R.styleable.DroidIconView_eyes_color, defaultEyeColor)
                    strokeColor =
                        getColor(R.styleable.DroidIconView_stroke_color, defaultStrokeColor)
                } finally {
                    recycle()
                }
            }

        bodyPaint.color = ColorUtils.setAlphaComponent(bodyColor, (alpha * 255).toInt())
        eyePaint.color = ColorUtils.setAlphaComponent(eyesColor, (alpha * 255).toInt())
        bodyStrokePaint.color = ColorUtils.setAlphaComponent(strokeColor, (alpha * 255).toInt())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val smallerDimension = if (bottom - top < right - left) {
            bottom - top
        } else {
            right - left
        }

        calculateRects(smallerDimension)
    }

    override fun onDraw(canvas: Canvas) {
        val corners = rectSize * 0.135f

        onPreDrawBody(canvas, bodyRect)
        drawLegsAndBody(canvas, corners)
        drawHead(canvas, corners)
        onPostDrawBody(canvas, bodyRect)
        drawHands(canvas, corners)
    }

    protected open fun onStartDrawLeftHand(canvas: Canvas, rect: RectF) {

    }

    protected open fun onStartDrawRightHand(canvas: Canvas, rect: RectF) {

    }

    protected open fun onPreDrawBody(canvas: Canvas, rect: RectF) {

    }

    protected open fun onPostDrawBody(canvas: Canvas, rect: RectF) {

    }

    private fun drawLegsAndBody(canvas: Canvas, corners: Float) {

        canvas.drawRoundRect(leftLegRect, corners, corners, bodyPaint)
        canvas.drawRoundRect(leftLegRect, corners, corners, bodyStrokePaint)

        canvas.drawRoundRect(rightLegRect, corners, corners, bodyPaint)
        canvas.drawRoundRect(rightLegRect, corners, corners, bodyStrokePaint)

        canvas.drawRoundRect(bodyRect, corners, corners, bodyPaint)
        canvas.drawRoundRect(bodyRect, corners, corners, bodyStrokePaint)
    }

    private fun drawHands(canvas: Canvas, corners: Float) {

        canvas.save()
        onStartDrawLeftHand(canvas, leftHandRect)
        canvas.drawRoundRect(leftHandRect, corners, corners, bodyPaint)
        canvas.drawRoundRect(leftHandRect, corners, corners, bodyStrokePaint)

        canvas.restore()

        canvas.save()
        onStartDrawRightHand(canvas, rightHandRect)
        canvas.drawRoundRect(rightHandRect, corners, corners, bodyPaint)
        canvas.drawRoundRect(rightHandRect, corners, corners, bodyStrokePaint)
        canvas.restore()
    }

    private fun drawHead(canvas: Canvas, corners: Float) {

        /**
         * Draw antenna
         */
        canvas.save()
        canvas.rotate(
            -15f, leftAntennaRect.right - (leftAntennaRect.right - leftAntennaRect.left) * 0.5f,
            leftAntennaRect.bottom
        )
        canvas.drawRoundRect(leftAntennaRect, corners, corners, bodyPaint)
        canvas.drawRoundRect(leftAntennaRect, corners, corners, bodyStrokePaint)

        canvas.restore()
        canvas.save()
        canvas.rotate(
            15f, rightAntennaRect.right - (rightAntennaRect.right - rightAntennaRect.left) * 0.5f,
            rightAntennaRect.bottom
        )
        canvas.drawRoundRect(rightAntennaRect, corners, corners, bodyPaint)
        canvas.drawRoundRect(rightAntennaRect, corners, corners, bodyStrokePaint)
        canvas.restore()

        /**
         * Draw head and eyes
         */
        val startAngle = 180f
        val sweepAngle = 180f
        val useCenter = true

        canvas.drawArc(headRect, startAngle, sweepAngle, useCenter, bodyPaint)
        canvas.drawArc(headRect, startAngle, sweepAngle, useCenter, bodyStrokePaint)

        canvas.drawCircle(leftEyePoint.x, leftEyePoint.y, eyeRadius, eyePaint)
        canvas.drawCircle(leftEyePoint.x, leftEyePoint.y, eyeRadius, bodyStrokePaint)

        canvas.drawCircle(rightEyePoint.x, rightEyePoint.y, eyeRadius, eyePaint)
        canvas.drawCircle(rightEyePoint.x, rightEyePoint.y, eyeRadius, bodyStrokePaint)
    }

    private fun calculateRects(smallerDimension: Int) {
        val rectSizeFactor = 0.22f
        rectSize = (smallerDimension * rectSizeFactor).toInt()

        bodyStrokePaint.strokeWidth = rectSize * STROKE_WIDTH_FACTOR
        val bodyShiftY = smallerDimension * 0.1f // put body lower than center of the view

        val wh = width * 0.5f
        halfHeightWithShift = height * 0.5f + bodyShiftY

        val rectSize75 = rectSize * 0.75f
        val rectSize25 = rectSize * 0.25f

        /**
         * Body
         */
        bodyRect.left = wh - rectSize
        bodyRect.right = wh + rectSize
        bodyRect.bottom = halfHeightWithShift - rectSize
        bodyRect.top = halfHeightWithShift + rectSize

        /**
         * Legs
         */
        val legShiftFactor = rectSizeFactor
        val legWFactor = rectSize * 0.22f

        val topLegY = bodyRect.top - rectSize25
        val bottomLegY = bodyRect.top + rectSize75

        leftLegRect.left = wh - legWFactor - wh * legShiftFactor
        leftLegRect.right = wh + legWFactor - wh * legShiftFactor
        leftLegRect.bottom = topLegY
        leftLegRect.top = bottomLegY

        rightLegRect.left = wh - legWFactor + wh * legShiftFactor
        rightLegRect.right = wh + legWFactor + wh * legShiftFactor
        rightLegRect.bottom = topLegY
        rightLegRect.top = bottomLegY

        /**
         * Hands
         */
        val handShiftFactor = rectSizeFactor * 2
        val handWFactor = rectSize * 0.18f
        val rectSizeH85 = rectSize * 0.65f

        val handBottom = halfHeightWithShift - rectSize75
        val handTop = halfHeightWithShift + rectSizeH85

        leftHandRect.left = wh - handWFactor - wh * handShiftFactor
        leftHandRect.right = wh + handWFactor - wh * handShiftFactor
        leftHandRect.bottom = handBottom
        leftHandRect.top = handTop

        rightHandRect.left = wh - handWFactor + wh * handShiftFactor
        rightHandRect.right = wh + handWFactor + wh * handShiftFactor
        rightHandRect.bottom = handBottom
        rightHandRect.top = handTop

        /**
         * Head
         */
        val bodyHeight = bodyRect.top - bodyRect.bottom

        headRect.left = (width - bodyHeight) * 0.5f
        headRect.right = headRect.left + bodyHeight
        val headMargin = bodyHeight * 0.05f
        headRect.top = bodyRect.bottom - bodyHeight * 0.5f - headMargin
        headRect.bottom = headRect.top + bodyHeight

        /**
         * Eyes
         */
        val eyeHorizontalShiftFactor = 0.3f
        eyeRadius = bodyHeight * 0.04f

        val eyesMargin = bodyHeight * 0.2f
        val cy = headRect.top + eyesMargin

        val leftCx = width * 0.5f + rectSize * eyeHorizontalShiftFactor
        val rightCx = width * 0.5f - rectSize * eyeHorizontalShiftFactor

        leftEyePoint.set(leftCx, cy)
        rightEyePoint.set(rightCx, cy)

        /**
         * Antenna
         */

        val antennaHeight = bodyHeight * 0.21f
        val antennaShiftFactor = 0.2f
        val antennaWFactor = rectSize * 0.1f

        val antennaShiftFactorY = bodyHeight * 0.08f
        val antennaBottom = headRect.top - antennaHeight + antennaShiftFactorY
        val antennaTop = headRect.top + antennaShiftFactorY

        leftAntennaRect.left = wh - antennaWFactor - wh * antennaShiftFactor
        leftAntennaRect.right = wh + antennaWFactor - wh * antennaShiftFactor
        leftAntennaRect.top = antennaTop
        leftAntennaRect.bottom = antennaBottom

        rightAntennaRect.left = wh - antennaWFactor + wh * antennaShiftFactor
        rightAntennaRect.right = wh + antennaWFactor + wh * antennaShiftFactor
        rightAntennaRect.top = antennaTop
        rightAntennaRect.bottom = antennaBottom
    }
}