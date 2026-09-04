package com.vitaltrace.app.feature.portalselector.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.core.session.PortalTarget
import com.vitaltrace.app.feature.home.presentation.components.LogoutConfirmationDialog
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalSelectorScreen(
    onSelectPortal: (PortalTarget) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: PortalSelectorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirmLogout by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it is PortalSelectorEffect.NavigateToLogin) onLoggedOut() }
    }

    // Android back must not silently pick a portal: it runs the same logout/exit flow
    // used elsewhere in the app.
    BackHandler { confirmLogout = true }

    if (confirmLogout) {
        LogoutConfirmationDialog(
            onConfirm = { confirmLogout = false; viewModel.logout() },
            onDismiss = { confirmLogout = false }
        )
    }

    Scaffold(
        containerColor = VitalTraceWarmBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "VitalTrace",
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { confirmLogout = true }) {
                        Icon(Icons.AutoMirrored.Rounded.ExitToApp, "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitalTraceWarmBackground,
                    titleContentColor = VitalTraceNavy,
                    actionIconContentColor = VitalTraceNavy
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                "¿Cómo deseas continuar?",
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Tu cuenta tiene acceso a más de un módulo. Elige con cuál quieres trabajar ahora.",
                color = Color(0xFF53636D),
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )
            state.portals.forEach { portal ->
                PortalCard(
                    portal = portal,
                    onClick = { onSelectPortal(portal) },
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun PortalCard(
    portal: PortalTarget,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presentation = portalPresentation(portal)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(48.dp), shape = CircleShape, color = Color(0xFFD9F2F0)) {
                Icon(presentation.icon, null, Modifier.padding(12.dp), tint = VitalTraceTeal)
            }
            Column(Modifier.padding(start = 16.dp)) {
                Text(
                    presentation.title,
                    color = VitalTraceNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    presentation.description,
                    color = Color(0xFF53636D),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private data class PortalPresentation(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private fun portalPresentation(portal: PortalTarget): PortalPresentation = when (portal) {
    PortalTarget.PATIENT -> PortalPresentation(
        Icons.Rounded.MonitorHeart,
        "Portal del paciente",
        "Consulta tus mediciones, citas y tratamientos."
    )
    PortalTarget.RELATIVE -> PortalPresentation(
        Icons.Rounded.People,
        "Portal familiar",
        "Sigue la evolución de tus pacientes autorizados."
    )
    PortalTarget.NURSE -> PortalPresentation(
        Icons.Rounded.MedicalServices,
        "Portal de enfermería",
        "Gestiona los pacientes que tienes asignados."
    )
}
