package com.vitaltrace.app.feature.treatments.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
        TreatmentStatus.ACTIVE -> Triple(R.string.treatments_status_active, Color(0xFFDDF4F2), VitalTraceTeal)
        TreatmentStatus.FINISHED -> Triple(R.string.treatments_status_finished, Color(0xFFDDF1E7), Color(0xFF23805F))
        TreatmentStatus.SUSPENDED -> Triple(R.string.treatments_status_suspended, Color(0xFFF3E4E1), Color(0xFF8C3D32))
        TreatmentStatus.UNKNOWN -> Triple(R.string.treatments_status_unknown, Color(0xFFE8E8E8), Color(0xFF53636D))
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
