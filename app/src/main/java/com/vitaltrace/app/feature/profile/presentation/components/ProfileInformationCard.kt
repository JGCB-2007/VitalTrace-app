package com.vitaltrace.app.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
            ProfileInformationRow(
                label = stringResource(R.string.profile_identifier),
                value = user.identifier
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileInformationRow(
                label = stringResource(R.string.profile_email),
                value = user.email
            )
            OptionalProfileInformationRow(stringResource(R.string.profile_phone), user.phone)
            OptionalProfileInformationRow(
                stringResource(R.string.profile_identification),
                user.identificationNumber
            )
            OptionalProfileInformationRow(
                stringResource(R.string.profile_birth_date),
                user.dateOfBirth
            )
            OptionalProfileInformationRow(
                stringResource(R.string.profile_age),
                user.age?.let { pluralStringResource(R.plurals.profile_age_value, it, it) }
            )
            OptionalProfileInformationRow(stringResource(R.string.profile_gender), user.gender)
            OptionalProfileInformationRow(stringResource(R.string.profile_address), user.address)
            OptionalProfileInformationRow(
                stringResource(R.string.profile_emergency_contact),
                user.emergencyContactName
            )
            OptionalProfileInformationRow(
                stringResource(R.string.profile_emergency_phone),
                user.emergencyContactPhone
            )
        }
    }
}

@Composable
private fun OptionalProfileInformationRow(label: String, value: String?) {
    value?.takeIf(String::isNotBlank)?.let {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ProfileInformationRow(label, it)
    }
}

@Composable
private fun ProfileInformationRow(label: String, value: String) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        val useStackedLayout = maxWidth < 420.dp

        if (useStackedLayout) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProfileInformationLabel(text = label)
                ProfileInformationValue(
                    text = value,
                    textAlign = TextAlign.Start
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileInformationLabel(
                    text = label,
                    modifier = Modifier.weight(0.42f)
                )
                ProfileInformationValue(
                    text = value,
                    modifier = Modifier.weight(0.58f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun ProfileInformationLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ProfileInformationValue(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = textAlign
    )
}
