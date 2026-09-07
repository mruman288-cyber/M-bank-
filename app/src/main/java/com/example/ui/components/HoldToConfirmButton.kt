package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryRose
import com.example.ui.theme.PrimaryRoseDark
import kotlinx.coroutines.launch

@Composable
fun HoldToConfirmButton(
    text: String,
    modifier: Modifier = Modifier,
    fillDurationMs: Int = 1200,
    onConfirmed: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    var isConfirmed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        PrimaryRose.copy(alpha = 0.2f),
                        PrimaryRoseDark.copy(alpha = 0.25f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        if (!isConfirmed) {
                            val job = coroutineScope.launch {
                                progress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(
                                        durationMillis = fillDurationMs,
                                        easing = LinearEasing
                                    )
                                )
                                if (progress.value >= 0.99f) {
                                    isConfirmed = true
                                    onConfirmed()
                                }
                            }
                            tryAwaitRelease()
                            job.cancel()
                            if (!isConfirmed) {
                                coroutineScope.launch {
                                    progress.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = 200)
                                    )
                                }
                            }
                        }
                    }
                )
            }
            .testTag("hold_to_confirm_button"),
        contentAlignment = Alignment.CenterStart
    ) {
        // Progress fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.value)
                .background(
                    Brush.horizontalGradient(
                        listOf(PrimaryRose, PrimaryRoseDark)
                    )
                )
        )

        // Text & Icon content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = "Hold",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
