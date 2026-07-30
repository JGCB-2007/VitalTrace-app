package com.vitaltrace.app.feature.relatives.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.vitaltrace.app.R

private data class StatusStyle(val label: Int, val container: Color, val content: Color)

@Composable
fun RelativeStatusChip(status: String, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = status,
        modifier = modifier,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "relativeStatus"
    ) { currentStatus ->
        val style = when (currentStatus) {
            "ACTIVE" -> StatusStyle(
                R.string.relatives_status_active,
                Color(0xFFDCEFE6),
                Color(0xFF1D6B4C)
            )
            "PENDING" -> StatusStyle(
                R.string.relatives_status_pending,
                Color(0xFFFFEBC8),
                Color(0xFF805B16)
            )
            "REVOKED" -> StatusStyle(
                R.string.relatives_status_revoked,
                Color(0xFFF5DDDF),
                Color(0xFF8E3E45)
            )
            "EXPIRED" -> StatusStyle(
                R.string.relatives_status_expired,
                Color(0xFFE5E9EC),
                Color(0xFF53636D)
            )
            else -> StatusStyle(
                R.string.relatives_status_not_authorized,
                Color(0xFFE5E9EC),
                Color(0xFF53636D)
            )
        }
        Box(
            modifier = Modifier
                .background(style.container, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Text(
                text = stringResource(style.label),
                color = style.content,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
