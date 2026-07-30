package com.vitaltrace.app.feature.relatives.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.relatives.presentation.RelativeUiModel
import com.vitaltrace.app.ui.theme.VitalTraceNavy

@Composable
fun RelativeCard(
    relative: RelativeUiModel,
    isActionInProgress: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RelativeAvatar()
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = relative.fullName?.takeIf(String::isNotBlank)
                            ?: stringResource(R.string.relatives_linked_relative),
                        color = VitalTraceNavy,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        relative.relationship,
                        color = Color(0xFF53636D),
                        fontSize = 15.sp
                    )
                }
            }
            relative.phone?.takeIf(String::isNotBlank)?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Phone,
                        contentDescription = null,
                        tint = Color(0xFF6C7A82),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(it, color = Color(0xFF53636D), fontSize = 15.sp)
                }
            }
            RelativeStatusChip(relative.status)
            HorizontalDivider(color = Color(0xFFE8E4DD))
            AnimatedContent(
                targetState = relative.isAuthorized,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "relativeAction"
            ) { isAuthorized ->
                if (isAuthorized) {
                    Button(
                        onClick = onAction,
                        enabled = !isActionInProgress,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB5484D),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFB5484D),
                            disabledContentColor = Color.White
                        )
                    ) {
                        ActionButtonContent(
                            loading = isActionInProgress,
                            label = stringResource(R.string.relatives_revoke),
                            progressColor = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = onAction,
                        enabled = !isActionInProgress,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VitalTraceNavy,
                            disabledContainerColor = Color(0xFFB8C0C5),
                            disabledContentColor = Color.White
                        )
                    ) {
                        ActionButtonContent(
                            loading = isActionInProgress,
                            label = stringResource(R.string.relatives_authorize),
                            progressColor = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonContent(loading: Boolean, label: String, progressColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = progressColor,
                strokeWidth = 2.dp
            )
        }
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}
