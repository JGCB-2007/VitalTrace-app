package com.vitaltrace.app.feature.auth.presentation.activation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.auth.presentation.recovery.RecoveryPasswordField
import com.vitaltrace.app.feature.auth.presentation.recovery.RecoveryPrimaryButton
import com.vitaltrace.app.feature.auth.presentation.recovery.RecoveryScreenLayout
import com.vitaltrace.app.feature.auth.presentation.recovery.RecoverySecondaryButton
import com.vitaltrace.app.feature.auth.presentation.recovery.RecoveryTextField

@Composable
fun FirstAccessEmailScreen(onBack: () -> Unit, onContinue: () -> Unit, viewModel: FirstAccessEmailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.continueFlow.collect { onContinue() } }
    RecoveryScreenLayout("Primer acceso", onBack) {
        Description("Ingresa el correo donde recibiste tu código temporal de acceso.")
        Spacer(Modifier.height(24.dp))
        RecoveryTextField(state.email, viewModel::onEmailChange, "Correo electrónico", Icons.Outlined.Email, true, keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
        ErrorText(state.error)
        RecoveryPrimaryButton("Continuar", false, viewModel::continueToCode, Modifier.padding(top = 20.dp))
    }
}

@Composable
fun ActivationCodeScreen(onBack: () -> Unit, onVerified: () -> Unit, viewModel: ActivationCodeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.verified.collect { onVerified() } }
    RecoveryScreenLayout("Verificar código", onBack) {
        Description("Escribe el código de 6 dígitos enviado a ${state.email}.")
        Spacer(Modifier.height(24.dp))
        RecoveryTextField(state.code, viewModel::onCodeChange, "Código de acceso", Icons.Outlined.Key, !state.isLoading, keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        ErrorText(state.error)
        state.resendMessage?.let { Text(it, color = Color(0xFF1D6B4C), modifier = Modifier.padding(top = 10.dp)) }
        RecoveryPrimaryButton("Verificar código", state.isLoading, viewModel::verify, Modifier.padding(top = 20.dp))
        RecoverySecondaryButton("Reenviar código", viewModel::resend)
    }
}

@Composable
fun CreateInitialPasswordScreen(onBack: () -> Unit, onSuccess: () -> Unit, viewModel: CreateInitialPasswordViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.completed.collect { onSuccess() } }
    RecoveryScreenLayout("Crear contraseña", onBack) {
        Description("Crea la contraseña que usarás para iniciar sesión en VitalTrace.")
        Spacer(Modifier.height(20.dp))
        Text("Usa al menos 8 caracteres, letras, números y un símbolo.", color = Color(0xFF5C6870), style = MaterialTheme.typography.bodyMedium)
        RecoveryPasswordField(state.password, viewModel::onPasswordChange, "Nueva contraseña", state.passwordVisible, viewModel::togglePassword, !state.isLoading, Modifier.padding(top = 20.dp))
        RecoveryPasswordField(state.confirmation, viewModel::onConfirmationChange, "Confirmar contraseña", state.confirmationVisible, viewModel::toggleConfirmation, !state.isLoading, Modifier.padding(top = 20.dp), ImeAction.Done)
        ErrorText(state.error)
        RecoveryPrimaryButton("Crear contraseña", state.isLoading, viewModel::submit, Modifier.padding(top = 20.dp))
    }
}

@Composable private fun Description(text: String) = Text(text, color = Color(0xFF5C6870), style = MaterialTheme.typography.bodyLarge)
@Composable private fun ErrorText(text: String?) { text?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 10.dp)) } }
