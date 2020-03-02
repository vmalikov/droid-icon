package com.vmdev.droidiconview

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet

private const val DEFAULT_HAND_ANIMATION_DURATION = 1000

open class AnimatedDroidIconView(context: Context, attributeSet: AttributeSet? = null) :
    DroidIconView(context, attributeSet) {

    var autoStartAnimation = false

    /**
     * left hand settings
     */
    var leftHandAngle: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    var leftHandAnimationDuration: Long
        get() = leftHandAnimator.duration
        set(value) {
            leftHandAnimator.duration = value
        }
    var leftHandRepeatCount: Int
        get() = leftHandAnimator.repeatCount
        set(value) {
            leftHandAnimator.repeatCount = value
        }
    var leftHandRepeatMode: Int
        get() = leftHandAnimator.repeatMode
        set(value) {
            leftHandAnimator.repeatMode = value
        }

    /**
     * right hand settings
     */

    var rightHandAngle: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    var rightHandAnimationDuration: Long
        get() = rightHandAnimator.duration
        set(value) {
            rightHandAnimator.duration = value
        }
    var rightHandRepeatCount: Int
        get() = rightHandAnimator.repeatCount
        set(value) {
            rightHandAnimator.repeatCount = value
        }
    var rightHandRepeatMode: Int
        get() = rightHandAnimator.repeatMode
        set(value) {
            rightHandAnimator.repeatMode = value
        }

    private val leftHandAnimator: ObjectAnimator

    private val rightHandAnimator: ObjectAnimator

    init {
        var lhDuration = DEFAULT_HAND_ANIMATION_DURATION
        var lhRepeatCount = ValueAnimator.INFINITE
        var lhRepeatMode = ValueAnimator.REVERSE
        var lhAngleFrom = 135
        var lhAngleTo = 200

        var rhDuration = DEFAULT_HAND_ANIMATION_DURATION
        var rhRepeatCount = 1
        var rhRepeatMode = ValueAnimator.REVERSE
        var rhAngleFrom = 0
        var rhAngleTo = 270

        context.theme.obtainStyledAttributes(attributeSet,
            R.styleable.DroidIconView, 0, 0)
            .apply {
                try {
                    autoStartAnimation = getBoolean(R.styleable.DroidIconView_auto_start_animation, false)
                    lhDuration = getInt(R.styleable.DroidIconView_left_hand_animation_duration, lhDuration)
                    lhRepeatCount = getInt(R.styleable.DroidIconView_left_hand_repeat_count, lhRepeatCount)
                    lhRepeatMode = getInt(R.styleable.DroidIconView_left_hand_repeat_mode, lhRepeatMode)
                    lhAngleFrom = getInt(R.styleable.DroidIconView_left_hand_angle_from, lhAngleFrom)
                    lhAngleTo = getInt(R.styleable.DroidIconView_left_hand_angle_to, lhAngleTo)

                    rhDuration = getInt(R.styleable.DroidIconView_right_hand_animation_duration, rhRepeatCount)
                    rhRepeatCount = getInt(R.styleable.DroidIconView_right_hand_repeat_count, rhRepeatCount)
                    rhRepeatMode = getInt(R.styleable.DroidIconView_right_hand_repeat_mode, rhRepeatMode)
                    rhAngleFrom = getInt(R.styleable.DroidIconView_right_hand_angle_from, rhAngleFrom)
                    rhAngleTo = getInt(R.styleable.DroidIconView_right_hand_angle_to, rhAngleTo)
                } finally {
                    recycle()
                }
            }

        leftHandAnimator = ObjectAnimator.ofFloat(this, "leftHandAngle", lhAngleFrom.toFloat(), lhAngleTo.toFloat())

        with(leftHandAnimator) {
            duration = lhDuration.toLong()
            repeatCount = lhRepeatCount
            repeatMode = lhRepeatMode
            addUpdateListener { invalidate() }
        }

        rightHandAnimator = ObjectAnimator.ofFloat(this, "rightHandAngle", rhAngleFrom.toFloat(), rhAngleTo.toFloat())
        with(rightHandAnimator) {
            duration = rhDuration.toLong()
            repeatCount = rhRepeatCount
            repeatMode = rhRepeatMode
            addUpdateListener { invalidate() }
        }

        if (autoStartAnimation) {
            startLeftHandAnimation()
        }
    }

    override fun onStartDrawLeftHand(canvas: Canvas, rect: RectF) {
        canvas.rotate(leftHandAngle, rect.right - (rect.right - rect.left) * 0.5f, rect.bottom)
    }

    override fun onStartDrawRightHand(canvas: Canvas, rect: RectF) {
        canvas.rotate(rightHandAngle, rect.right - (rect.right - rect.left) * 0.5f, rect.bottom)
    }

    fun startLeftHandAnimation() {
        leftHandAnimator.start()
    }

    fun stopLeftHandAnimation() {
        leftHandAnimator.pause()
    }

    fun startRightHandAnimation() {
        rightHandAnimator.start()
    }

    fun stopRightHandAnimation() {
        rightHandAnimator.pause()
    }
}