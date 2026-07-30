package com.vitaltrace.app.feature.auth.presentation.recovery

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onEnterToken: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RecoveryScreenLayout(
        title = stringResource(R.string.forgot_password_title),
        onBack = onBack
    ) {
        Text(
            stringResource(R.string.forgot_password_description),
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
            placeholder = stringResource(R.string.login_email_placeholder),
            keyboardType = KeyboardType.Email,
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
        if (state.isSuccess) {
            Surface(
                color = Color(0xFFDCEFE6),
                shape = RecoveryFieldShape,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                Text(
                    stringResource(R.string.forgot_password_success),
                    color = Color(0xFF1D6B4C),
                    modifier = Modifier.padding(18.dp)
                )
            }
        }
        RecoveryPrimaryButton(
            text = stringResource(R.string.forgot_password_submit),
            isLoading = state.isLoading,
            onClick = viewModel::submit,
            modifier = Modifier.padding(top = 18.dp)
        )
        RecoverySecondaryButton(
            text = stringResource(R.string.forgot_password_have_token),
            onClick = onEnterToken
        )
    }
}
