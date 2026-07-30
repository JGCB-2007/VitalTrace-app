package com.vitaltrace.app.feature.auth.presentation.recovery

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.R

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
        Spacer(Modifier.height(28.dp))
        RecoveryTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = stringResource(R.string.password_recovery_email),
            leadingIcon = Icons.Outlined.Email,
            enabled = !state.isLoading,
            keyboardType = KeyboardType.Email
        )
        RecoveryTextField(
            value = state.token,
            onValueChange = viewModel::onTokenChange,
            label = stringResource(R.string.reset_password_token),
            leadingIcon = Icons.Outlined.Key,
            enabled = !state.isLoading,
            modifier = Modifier.padding(top = 20.dp)
        )
        RecoveryPasswordField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = stringResource(R.string.reset_password_new),
            visible = state.isPasswordVisible,
            onToggle = viewModel::togglePasswordVisibility,
            enabled = !state.isLoading,
            modifier = Modifier.padding(top = 20.dp)
        )
        RecoveryPasswordField(
            value = state.passwordConfirmation,
            onValueChange = viewModel::onConfirmationChange,
            label = stringResource(R.string.reset_password_confirmation),
            visible = state.isConfirmationVisible,
            onToggle = viewModel::toggleConfirmationVisibility,
            enabled = !state.isLoading,
            modifier = Modifier.padding(top = 20.dp),
            imeAction = ImeAction.Done
        )
        state.errorMessage?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        RecoveryPrimaryButton(
            text = stringResource(R.string.reset_password_submit),
            isLoading = state.isLoading,
            onClick = viewModel::submit,
            modifier = Modifier.padding(top = 18.dp)
        )
    }
}
