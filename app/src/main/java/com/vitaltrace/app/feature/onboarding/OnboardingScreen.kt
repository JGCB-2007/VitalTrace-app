package com.vitaltrace.app.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class OnboardingPage(
    val icon: ImageVector,
    val eyebrow: String,
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pages = remember {
        listOf(
            OnboardingPage(
                Icons.Rounded.Favorite,
                "TU SALUD, MÁS CLARA",
                "Todo tu seguimiento en un solo lugar",
                "Consulta mediciones, citas y tratamientos con una vista sencilla y organizada."
            ),
            OnboardingPage(
                Icons.Rounded.NotificationsActive,
                "A TU RITMO",
                "Recordatorios que sí puedes controlar",
                "Elige cuándo recibir avisos para registrar tus mediciones y prepararte para tus citas."
            ),
            OnboardingPage(
                Icons.Rounded.Security,
                "PRIVACIDAD PRIMERO",
                "Tu información permanece protegida",
                "Activa el bloqueo biométrico y evita que contenido sensible aparezca en capturas recientes."
            )
        )
    }
    var page by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onComplete) { Text("Omitir") }
        }
        AnimatedContent(
            targetState = page,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
            modifier = Modifier.weight(1f),
            label = "onboarding"
        ) { index ->
            val item = pages[index]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(132.dp),
                    shape = RoundedCornerShape(38.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(32.dp)
                    )
                }
                Spacer(Modifier.height(38.dp))
                Text(
                    item.eyebrow,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    item.title,
                    modifier = Modifier.padding(top = 14.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    item.description,
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pages.indices.forEach { index ->
                Box(
                    Modifier
                        .size(if (index == page) 12.dp else 8.dp)
                        .background(
                            if (index == page) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                            CircleShape
                        )
                )
            }
        }
        Button(
            onClick = {
                if (page == pages.lastIndex) onComplete() else page++
            },
            modifier = Modifier.fillMaxWidth().padding(top = 26.dp).height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(if (page == pages.lastIndex) "Comenzar" else "Continuar", fontWeight = FontWeight.Bold)
        }
    }
}
