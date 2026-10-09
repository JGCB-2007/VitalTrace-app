package com.vitaltrace.app.feature.profile.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.vitaltrace.app.ui.theme.SoraFontFamily
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.profile.presentation.ProfileUserUiModel
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileIdentityCard(
    user: ProfileUserUiModel,
    modifier: Modifier = Modifier,
    roleLabel: String? = null,
    avatarUri: String? = null,
    onAvatarClick: (() -> Unit)? = null
) {
    val role = roleLabel ?: stringResource(R.string.profile_role_patient)
    val description = stringResource(
        R.string.profile_identity_description,
        user.fullName,
        role
    )
    val context = LocalContext.current
    val avatar by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, avatarUri) {
        value = avatarUri?.let { value ->
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(value.toUri())?.use {
                        BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = description },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .then(if (onAvatarClick != null) Modifier.clickable(onClick = onAvatarClick) else Modifier)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(VitalTraceTeal, VitalTraceNavy)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (avatar != null) {
                    Image(
                        bitmap = requireNotNull(avatar),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = user.initials,
                        color = Color.White,
                        fontFamily = SoraFontFamily,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                if (onAvatarClick != null) {
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).size(30.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Icon(
                            Icons.Rounded.PhotoCamera,
                            contentDescription = "Cambiar foto",
                            modifier = Modifier.padding(6.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = user.fullName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = SoraFontFamily,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = role,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
