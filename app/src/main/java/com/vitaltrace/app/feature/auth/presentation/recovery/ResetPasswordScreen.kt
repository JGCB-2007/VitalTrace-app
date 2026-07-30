package com.vitaltrace.app.feature.auth.presentation.recovery

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceNavy

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect {
            if (it == ResetPasswordEffect.NavigateToLogin) onSuccess()
        }
    }
    RecoveryScreenLayout(
        title = stringResource(R.string.reset_password_title),
        onBack = onBack
    ) {
        Text(
            stringResource(R.string.reset_password_description),
            color = Color(0xFF5C6870),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(24.dp))
        RecoveryTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = stringResource(R.string.password_recovery_email),
            icon = { Icon(Icons.Outlined.Email, null) },
            keyboardType = KeyboardType.Email,
            enabled = !state.isLoading
        )
        RecoveryTextField(
            value = state.token,
            onValueChange = viewModel::onTokenChange,
            label = stringResource(R.string.reset_password_token),
            icon = { Icon(Icons.Outlined.Key, null) },
            enabled = !state.isLoading
        )
        RecoveryPasswordField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = stringResource(R.string.reset_password_new),
            visible = state.isPasswordVisible,
            onToggle = viewModel::togglePasswordVisibility,
            enabled = !state.isLoading
        )
        RecoveryPasswordField(
            value = state.passwordConfirmation,
            onValueChange = viewModel::onConfirmationChange,
            label = stringResource(R.string.reset_password_confirmation),
            visible = state.isConfirmationVisible,
            onToggle = viewModel::toggleConfirmationVisibility,
            enabled = !state.isLoading
        )
        state.errorMessage?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        Button(
            onClick = viewModel::submit,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(58.dp),
            shape = RecoveryFieldShape,
            colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)
        ) {
            if (state.isLoading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            else Text(stringResource(R.string.reset_password_submit), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecoveryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean
) {
    Text(label, color = VitalTraceNavy, fontWeight = FontWeight.Bold)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 18.dp),
        enabled = enabled,
        leadingIcon = icon,
        singleLine = true,
        shape = RecoveryFieldShape,
        colors = recoveryFieldColors(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next)
    )
}

@Composable
private fun RecoveryPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean
) {
    Text(label, color = VitalTraceNavy, fontWeight = FontWeight.Bold)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 18.dp),
        enabled = enabled,
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        trailingIcon = {
            IconButton(onClick = onToggle, enabled = enabled) {
                Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null)
            }
        },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None
        else PasswordVisualTransformation(),
        shape = RecoveryFieldShape,
        colors = recoveryFieldColors(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        )
    )
}
