package com.vitaltrace.app.feature.relatives.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun RelativeAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(54.dp)
            .background(Color(0xFFD9F2F0), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            tint = VitalTraceTeal,
            modifier = Modifier.size(28.dp)
        )
    }
}
