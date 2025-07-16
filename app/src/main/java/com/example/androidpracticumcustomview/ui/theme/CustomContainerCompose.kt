package com.example.androidpracticumcustomview.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.androidpracticumcustomview.CHILD_ADD_ALPHA
import com.example.androidpracticumcustomview.CHILD_END_ALPHA
import com.example.androidpracticumcustomview.DEFAULT_ALPHA_ANIMATION_DURATION
import com.example.androidpracticumcustomview.DEFAULT_MOVEMENT_ANIMATION_DURATION
import kotlinx.coroutines.launch

@Composable
fun CustomContainerCompose(
    firstChild: @Composable (() -> Unit)?,
    secondChild: @Composable (() -> Unit)?,
    animationAlphaDuration: Int = DEFAULT_ALPHA_ANIMATION_DURATION,
    animationMovementDuration: Int = DEFAULT_MOVEMENT_ANIMATION_DURATION,
) {
    val alphaAnimate = remember { Animatable(CHILD_ADD_ALPHA) }
    val offsetYFirst = remember { Animatable(0f) }
    val offsetYSecond = remember { Animatable(0f) }
    val fullHeight = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        launch {
            alphaAnimate.animateTo(
                targetValue = CHILD_END_ALPHA,
                animationSpec = tween(durationMillis = animationAlphaDuration)
            )
        }

        launch {
            fullHeight.floatValue.let { h ->
                offsetYFirst.animateTo(
                    targetValue = -h / 2f - 50f,
                    animationSpec = tween(durationMillis = animationMovementDuration)
                )
            }
        }

        launch {
            fullHeight.floatValue.let { h ->
                offsetYSecond.animateTo(
                    targetValue = h / 2f - 50f,
                    animationSpec = tween(durationMillis = animationMovementDuration)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                fullHeight.floatValue = it.size.height.toFloat()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        firstChild?.let {
            AlphaWrapper(
                alpha = alphaAnimate.value,
                translationY = offsetYFirst.value,
                content = firstChild
            )
        }
        secondChild?.let {
            AlphaWrapper(
                alpha = alphaAnimate.value,
                translationY = offsetYSecond.value,
                content = secondChild
            )
        }
    }
}

@Composable
private fun AlphaWrapper(alpha: Float, translationY: Float, content: @Composable () -> Unit) {
    Box(modifier = Modifier.graphicsLayer {
        this.alpha = alpha
        this.translationY = translationY
    }) {
        content()
    }
}