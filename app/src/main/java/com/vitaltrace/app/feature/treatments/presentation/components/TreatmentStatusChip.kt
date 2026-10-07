package com.vitaltrace.app.feature.treatments.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.treatments.presentation.TreatmentStatus
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun TreatmentStatusChip(status: TreatmentStatus, modifier: Modifier = Modifier) {
    val presentation = when (status) {
        TreatmentStatus.ACTIVE -> Triple(R.string.treatments_status_active, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        TreatmentStatus.FINISHED -> Triple(R.string.treatments_status_finished, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        TreatmentStatus.SUSPENDED -> Triple(R.string.treatments_status_suspended, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        TreatmentStatus.UNKNOWN -> Triple(R.string.treatments_status_unknown, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Surface(modifier = modifier, color = presentation.second, shape = RoundedCornerShape(50)) {
        Text(
            text = stringResource(presentation.first),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = presentation.third,
            fontWeight = FontWeight.Bold
        )
    }
}
