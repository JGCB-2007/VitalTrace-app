package com.vitaltrace.app.core.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSkeleton(
    description: String,
    modifier: Modifier = Modifier,
    cards: Int = 4
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
        label = "skeleton-alpha"
    )
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.18f)
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp).semantics { contentDescription = description },
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box(Modifier.fillMaxWidth(0.55f).height(34.dp).clip(RoundedCornerShape(12.dp)).background(color))
        repeat(cards) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(52.dp).clip(CircleShape).background(color))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Box(Modifier.fillMaxWidth(0.65f).height(17.dp).clip(RoundedCornerShape(7.dp)).background(color))
                        Box(Modifier.fillMaxWidth(0.42f).height(13.dp).clip(RoundedCornerShape(7.dp)).background(color))
                    }
                }
                Box(Modifier.fillMaxWidth().height(13.dp).clip(RoundedCornerShape(7.dp)).background(color))
                Box(Modifier.width(140.dp).height(13.dp).clip(RoundedCornerShape(7.dp)).background(color))
            }
        }
    }
}
