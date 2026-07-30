package com.vitaltrace.app.feature.relatives.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = relative.fullName?.takeIf(String::isNotBlank)
                    ?: stringResource(R.string.relatives_linked_relative),
                color = VitalTraceNavy,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(relative.relationship, color = Color(0xFF53636D), fontSize = 16.sp)
            relative.phone?.takeIf(String::isNotBlank)?.let {
                Text(it, color = Color(0xFF53636D), fontSize = 15.sp)
            }
            Text(
                text = if (relative.isAuthorized) {
                    stringResource(R.string.relatives_status_authorized)
                } else {
                    stringResource(R.string.relatives_status_not_authorized)
                },
                color = if (relative.isAuthorized) Color(0xFF2E7D5B) else Color(0xFF8B5E34),
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = onAction,
                enabled = !isActionInProgress,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (relative.isAuthorized) Color(0xFF8E3E45) else VitalTraceNavy,
                    disabledContainerColor = Color(0xFFB8C0C5),
                    disabledContentColor = Color.White
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isActionInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(2.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                    Text(
                        if (relative.isAuthorized) {
                            stringResource(R.string.relatives_revoke)
                        } else {
                            stringResource(R.string.relatives_authorize)
                        }
                    )
                }
            }
        }
    }
}
