package com.example.androidpracticumcustomview.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.androidpracticumcustomview.CHILD_ADD_ALPHA
import com.example.androidpracticumcustomview.CHILD_END_ALPHA
import com.example.androidpracticumcustomview.DEFAULT_ALPHA_ANIMATION_DURATION
import com.example.androidpracticumcustomview.DEFAULT_MOVEMENT_ANIMATION_DURATION
import com.example.androidpracticumcustomview.R

class CustomContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
    private var animationAlphaDuration: Long = DEFAULT_ALPHA_ANIMATION_DURATION.toLong()
    private var animationMovementDuration: Long = DEFAULT_MOVEMENT_ANIMATION_DURATION.toLong()

    init {
        setWillNotDraw(false)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomContainer)

            animationAlphaDuration = typedArray.getInt(
                R.styleable.CustomContainer_alphaDuration,
                DEFAULT_ALPHA_ANIMATION_DURATION
            ).toLong()

            animationMovementDuration = typedArray.getInt(
                R.styleable.CustomContainer_movementDuration,
                DEFAULT_MOVEMENT_ANIMATION_DURATION
            ).toLong()

            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        repeat(childCount) {
            val child = getChildAt(it)

            runCatching { measureChild(child, widthMeasureSpec, heightMeasureSpec) }
                .onFailure { exception ->
                    exception.printStackTrace()
                    removeView(child)
                }
        }

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        repeat(childCount) {
            val child = getChildAt(it)
            if (child.visibility == View.GONE) return@repeat

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val childLeft = (width - childWidth - child.paddingLeft - child.paddingRight) / 2
            val childRight = childLeft + childWidth
            val childTop = (height - childHeight - child.paddingTop - child.paddingBottom) / 2
            val childBottom = childTop + childHeight

            runCatching { child.layout(childLeft, childTop, childRight, childBottom) }
                .onFailure { exception ->
                    exception.printStackTrace()
                    removeView(child)
                }
        }
    }

    override fun addView(child: View) {
        if (childCount == MAX_CHILD_COUNT) {
            throw IllegalStateException("Can host only $MAX_CHILD_COUNT children")
        }

        child.alpha = CHILD_ADD_ALPHA
        super.addView(child)
        child.post {
            startAlphaAnimation(child)
            startMovementAnimation(child)
        }
    }

    private fun startMovementAnimation(child: View) {
        val index = indexOfChild(child)
        val offset = when (index) {
            0 -> -child.top.toFloat()
            else -> height - child.bottom.toFloat()
        }

        child.animate()
            .translationY(offset)
            .setDuration(animationMovementDuration)
            .start()
    }

    private fun startAlphaAnimation(child: View) {
        child.animate()
            .alpha(CHILD_END_ALPHA)
            .setDuration(animationAlphaDuration)
            .start()
    }

    /** Устанавливает длительность анимации изменения прозрачности в миллисекундах */
    fun setAlphaAnimationDuration(durationMs: Long) {
        animationAlphaDuration = durationMs
    }

    /** Устанавливает длительность анимации перемещения элемента в миллисекундах */
    fun setMovementAnimationDuration(durationMs: Long) {
        animationMovementDuration = durationMs
    }

    companion object {
        private const val MAX_CHILD_COUNT = 2

    }
}