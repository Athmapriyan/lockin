package com.lockin.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.app.ui.theme.Indigo500
import com.lockin.app.ui.theme.StrokeSubtle

/** PIN dot indicators (4 slots) with shake animation on error */
@Composable
fun PinDots(pinLength: Int, hasError: Boolean, modifier: Modifier = Modifier) {
    val shakeOffset by animateFloatAsState(
        targetValue = if (hasError) 1f else 0f,
        animationSpec = if (hasError) spring(dampingRatio = Spring.DampingRatioHighBouncy) else snap(),
        label = "shake"
    )
    val offsetX by rememberInfiniteTransition(label = "shake_loop").animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(60), RepeatMode.Reverse),
        label = "shake_x"
    )

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(4) { index ->
            val filled = index < pinLength
            Box(
                modifier = Modifier
                    .offset(x = if (hasError) offsetX.dp * 0.3f else 0.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (filled) Indigo500 else Color.Transparent)
                    .border(2.dp, if (filled) Indigo500 else StrokeSubtle, CircleShape)
            )
        }
    }
}

/** Numeric keypad with scale press animation */
@Composable
fun NumericKeypad(
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        keys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    when {
                        key.isEmpty() -> Spacer(Modifier.size(80.dp))
                        key == "⌫" -> {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onBackspace()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Backspace,
                                    contentDescription = "Backspace",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        else -> {
                            var pressed by remember { mutableStateOf(false) }
                            val scale by animateFloatAsState(if (pressed) 0.9f else 1f, label = "key_$key")
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onKey(key)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
