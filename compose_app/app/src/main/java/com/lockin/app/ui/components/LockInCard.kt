package com.lockin.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lockin.app.ui.theme.StrokeSubtle

/** iOS-style frosted-glass grouped card — the core design unit */
@Composable
fun LockInCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed && onClick != null) 0.97f else 1f, label = "card_scale")

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(cornerRadius), spotColor = Color.Black.copy(alpha = 0.12f))
            .clip(RoundedCornerShape(cornerRadius))
            .border(0.5.dp, StrokeSubtle, RoundedCornerShape(cornerRadius))
            .then(if (onClick != null) Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick) else Modifier),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}
