package com.lockin.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.app.model.TaskItem
import com.lockin.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(
    task: TaskItem,
    isPrivacyMode: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val displayTitle = if (isPrivacyMode && task.isPrivate) "\uD83D\uDD12 Private Task" else task.title
    val dateStr = SimpleDateFormat("MMM d, yyyy  •  h:mm a", Locale.getDefault()).format(Date(task.dateMillis))

    val checkColor by animateColorAsState(
        if (task.completed) Success else Color.Transparent,
        animationSpec = tween(300),
        label = "check_color"
    )
    val checkBorder by animateColorAsState(
        if (task.completed) Success else StrokeSubtle,
        animationSpec = tween(300),
        label = "check_border"
    )
    val scoreScale by animateFloatAsState(
        if (task.completed) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "check_scale"
    )

    LockInCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Checkmark circle
            Box(
                modifier = Modifier
                    .scale(scoreScale)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(checkColor)
                    .border(2.dp, checkBorder, CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (task.completed) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.completed) TextSecondary else TextPrimary,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                )
            }

            if (task.isPrivate && !isPrivacyMode) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Outlined.Lock,
                    contentDescription = "Private",
                    tint = Yellow400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
