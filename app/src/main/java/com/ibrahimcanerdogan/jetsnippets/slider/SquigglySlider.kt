package com.ibrahimcanerdogan.jetsnippets.slider

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibrahimcanerdogan.jetsnippets.ui.theme.JetSnippetsTheme
import com.ibrahimcanerdogan.jetsnippets.ui.theme.Purple80
import kotlin.math.sin

const val PI = Math.PI.toFloat()
fun Int.pi(): Float = this * PI

@Composable
fun SquigglySlider(
    stateSlider: Float,
    onSliderUpdate: (Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val wave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.fillMaxWidth()
            .height(35.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        val x = event.changes.last().position.x.coerceIn(
                            minimumValue = 0f,
                            maximumValue = size.width.toFloat()
                        )
                        val normalizedX = x / size.width
                        onSliderUpdate(normalizedX)
                    } while (event.changes.none { it.changedToUp() })
                }
            }
            .drawBehind {
                val padding = 16.dp.toPx()
                val wavelength = 48.dp.toPx()
                val amplitude = 4.dp.toPx()
                val yShift = size.height / 2
                val phase = wave * 2.pi()
                val end = size.width - padding
                val actualWidth = size.width - padding * 2

                val segment = wavelength / 10f
                val numSegments = (actualWidth / segment).toInt()
                val collectedPoints = mutableListOf<Offset>()

                var pointX = padding
                val path = Path().apply {
                    for (point in 0..numSegments) {
                        val b = 2.pi() / wavelength
                        val pointY = amplitude * sin((b * pointX) - phase) + yShift

                        when (point) {
                            0 -> moveTo(pointX, pointY)
                            else -> lineTo(pointX, pointY)
                        }

                        collectedPoints.add(Offset(pointX, pointY))
                        pointX += segment
                    }
                }
                clipRect(right = size.width * stateSlider) {
                    drawPath(
                        path = path,
                        color = Purple80,
                        style = Stroke(
                            width = 12f,
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.cornerPathEffect(radius = amplitude)
                        )
                    )
                }
                clipRect(left = size.width * stateSlider) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(padding, yShift),
                        end = Offset(end, yShift),
                        strokeWidth = 5f,
                        cap = StrokeCap.Round
                    )
                }
                val circleX = (size.width * stateSlider).coerceIn(padding, end)
                drawCircle(
                    color = Purple80,
                    radius = padding,
                    center = Offset(circleX, yShift)
                )
            }
    )
}

@Preview
@Composable
private fun SquigglySliderPreview() {
    JetSnippetsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            var sliderValue by remember { mutableFloatStateOf(0.5F) }
            SquigglySlider(
                stateSlider = sliderValue,
                onSliderUpdate = { sliderValue = it }
            )
        }
    }
}
