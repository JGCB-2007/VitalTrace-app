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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RelativeAvatar(modifier = Modifier.size(46.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = relative.fullName?.takeIf(String::isNotBlank)
                            ?: stringResource(R.string.relatives_linked_relative),
                        color = VitalTraceNavy,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(relative.relationship, color = Color(0xFF53636D), fontSize = 14.sp)
                }
                RelativeStatusChip(relative.status)
            }

            relative.phone?.takeIf(String::isNotBlank)?.let { phone ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Phone,
                        contentDescription = null,
                        tint = Color(0xFF6C7A82),
                        modifier = Modifier.size(17.dp)
                    )
                    Text(phone, color = Color(0xFF53636D), fontSize = 14.sp)
                }
            }

            AnimatedContent(
                targetState = relative.status,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "relativeAction"
            ) { status ->
                when (status) {
                    "PENDING" -> RelativeActionButton(
                        label = stringResource(R.string.relatives_authorize),
                        loading = isActionInProgress,
                        containerColor = VitalTraceNavy,
                        onClick = onAction
                    )
                    "ACTIVE" -> RelativeActionButton(
                        label = stringResource(R.string.relatives_revoke),
                        loading = isActionInProgress,
                        containerColor = Color(0xFFB5484D),
                        onClick = onAction
                    )
                    "REVOKED", "EXPIRED" -> Unit
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun RelativeActionButton(
    label: String,
    loading: Boolean,
    containerColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White,
            disabledContainerColor = containerColor.copy(alpha = 0.55f),
            disabledContentColor = Color.White
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(17.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}
