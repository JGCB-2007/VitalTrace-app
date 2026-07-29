package com.vitaltrace.app.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.profile.presentation.ProfileUserUiModel

@Composable
fun ProfileInformationCard(
    user: ProfileUserUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
            ProfileInformationRow(
                label = stringResource(R.string.profile_identifier),
                value = user.identifier
            )
            HorizontalDivider(color = Color(0xFFE5E0D7))
            ProfileInformationRow(
                label = stringResource(R.string.profile_email),
                value = user.email
            )
            HorizontalDivider(color = Color(0xFFE5E0D7))
            ProfileInformationRow(
                label = stringResource(R.string.profile_phone),
                value = user.phone
            )
        }
    }
}

@Composable
private fun ProfileInformationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF53636D),
            fontSize = 17.sp
        )
        Text(
            text = value,
            modifier = Modifier
                .weight(1f)
                .padding(start = 18.dp),
            color = Color(0xFF172C3A),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}
