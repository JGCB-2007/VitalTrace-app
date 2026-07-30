package com.vitaltrace.app.feature.auth.presentation.recovery

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceNavy

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
        Text(
            stringResource(R.string.password_recovery_email),
            color = VitalTraceNavy,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            enabled = !state.isLoading,
            leadingIcon = { Icon(Icons.Outlined.Email, null) },
            placeholder = { Text(stringResource(R.string.login_email_placeholder)) },
            singleLine = true,
            shape = RecoveryFieldShape,
            colors = recoveryFieldColors(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
        )
        state.errorMessage?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        if (state.isSuccess) {
            Surface(
                color = Color(0xFFDCEFE6),
                shape = RecoveryFieldShape,
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp)
            ) {
                Text(
                    stringResource(R.string.forgot_password_success),
                    color = Color(0xFF1D6B4C),
                    modifier = Modifier.padding(18.dp)
                )
            }
        }
        Button(
            onClick = viewModel::submit,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(58.dp),
            shape = RecoveryFieldShape,
            colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.forgot_password_submit), fontWeight = FontWeight.Bold)
            }
        }
        TextButton(onClick = onEnterToken, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.forgot_password_have_token))
        }
    }
}
