package com.ibrahimcanerdogan.jetsnippets.switchx

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibrahimcanerdogan.jetsnippets.ui.theme.JetSnippetsTheme

@Composable
fun ThemeSwitcher(
    darkTheme: Boolean,
    onToggle: () -> Unit
) {
    val transition = updateTransition(targetState = darkTheme, label = "ThemeTransition")

    val thumbOffset by transition.animateDp(label = "ThumbOffset") {
        if (it) 24.dp else 0.dp
    }
    val backgroundColor by transition.animateColor(label = "BackgroundColor") {
        if (it) Color.DarkGray else Color.LightGray
    }
    val thumbColor by transition.animateColor(label = "ThumbColor") {
        if (it) Color(0xFFFFD700) else Color.White
    }

    Box(
        modifier = Modifier
            .size(width = 58.dp, height = 32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onToggle() }
            .padding(all = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .offset(x = thumbOffset)
                .clip(CircleShape)
                .background(thumbColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = "Theme Icon",
                tint = if (darkTheme) Color.White else Color(0xFFFFA500),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ThemeSwitcherPreview(
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    var isDarkMode by rememberSaveable(darkTheme) { mutableStateOf(darkTheme) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isDarkMode) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 500), label = "BackgroundAnimation"
    )

    JetSnippetsTheme (darkTheme = isDarkMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            ThemeSwitcher(
                darkTheme = isDarkMode,
                onToggle = { isDarkMode = !isDarkMode }
            )
        }
    }
}