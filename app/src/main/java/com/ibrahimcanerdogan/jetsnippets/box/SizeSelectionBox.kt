package com.ibrahimcanerdogan.jetsnippets.box

import android.graphics.Path
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ibrahimcanerdogan.jetsnippets.ui.theme.JetSnippetsTheme
import com.ibrahimcanerdogan.jetsnippets.ui.theme.Purple40
import com.ibrahimcanerdogan.jetsnippets.ui.theme.Purple80
import kotlin.math.roundToInt

data class SizeState(
    val stateStartPosition: Int,
    val stateAmount: Int,
    val stateSelected: Boolean,
    val stateIsSwappable: Boolean
)

@Composable
fun ConstantSizeSelection(
    modifier: Modifier = Modifier,
    sizeState: SizeState,
    onSizeSelect: (SizeState) -> Unit
) {

    val progress by animateFloatAsState(
        targetValue = if (sizeState.stateSelected) 0f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
    )

    val scale by animateFloatAsState(
        targetValue = if (sizeState.stateSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )

    JetSnippetsTheme {
        Box(
            modifier = modifier
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .fillMaxWidth()
                .aspectRatio(3 / 5f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onSizeSelect(sizeState) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Purple40)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ClipShape(progress))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Purple80)
                        .border(width = 1.dp, shape = RoundedCornerShape(16.dp), color = Purple40)
                )
            }
        }
    }
}

@Composable
fun SwappableSizeSelection(
    modifier: Modifier = Modifier,
    sizeState: SizeState,
    onSizeUpdate: (SizeState) -> Unit
) {
    val max = 128f
    var progress by remember { mutableFloatStateOf((max - sizeState.stateAmount) / max) }

    val scale by animateFloatAsState(
        targetValue = if (sizeState.stateSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )

    val amount = remember(progress) { ((1 - progress) * max).roundToInt() }

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .fillMaxWidth()
            .aspectRatio(3 / 5f)
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        val clampedY = event.changes.last().position.y.coerceIn(
                            minimumValue = 0f,
                            maximumValue = size.height.toFloat()
                        )
                        val normalizedY = clampedY / size.height.toFloat()
                        progress = normalizedY
                    } while (event.changes.none { it.changedToUp() })
                    onSizeUpdate(sizeState.copy(stateAmount = ((1 - progress) * max).roundToInt()))
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Purple40),
            contentAlignment = Alignment.Center
        ) {
            Text(amount.toString(), color = Color.White, fontSize = 16.sp)
        }

        Box(
            modifier = Modifier.clip(ClipShape(progress))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Purple80)
                    .border(width = 1.dp, shape = RoundedCornerShape(16.dp), color = Purple40),
                contentAlignment = Alignment.Center
            ) {
                Text(amount.toString(), color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

class ClipShape(private val progress: Float = 0f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            addRect(
                0f,
                0f,
                size.width,
                size.height * progress,
                Path.Direction.CW
            )
        }
        return Outline.Generic(path.asComposePath())
    }
}

@Composable
private fun SizeSelectionGroup(
    states: List<SizeState>,
    action: (SizeState) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        states.forEach { state ->
            if (state.stateIsSwappable) {
                SwappableSizeSelection(
                    modifier = Modifier.weight(1f),
                    sizeState = state,
                    onSizeUpdate = action
                )
            } else {
                ConstantSizeSelection(
                    modifier = Modifier.weight(1f),
                    sizeState = state,
                    onSizeSelect = action
                )
            }
        }
    }
}

@Preview
@Composable
fun SizeSelectionGroupPreview() {
    var states by remember {
        mutableStateOf(
            listOf(
                SizeState(0, 8, false, stateIsSwappable = false),
                SizeState(1, 16, false, stateIsSwappable = true)
            )
        )
    }

    fun selectStateSize(selectedSize: SizeState) {
        states = states.map { state ->
            state.copy(stateSelected = state == selectedSize)
        }
    }

    JetSnippetsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            SizeSelectionGroup(states = states, action = ::selectStateSize)
        }
    }
}
