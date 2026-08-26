package com.vitaltrace.app.feature.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.auth.presentation.components.LoginBrandHeader
import com.vitaltrace.app.feature.auth.presentation.components.LoginForm
import com.vitaltrace.app.feature.auth.presentation.components.LoginSupportNotice
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onActivationRequired: () -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    onFirstAccessClick: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                LoginUiEffect.NavigateToHome -> onLoginSuccess()
                LoginUiEffect.NavigateToFirstAccess -> onActivationRequired()
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordVisibilityChange = viewModel::onPasswordVisibilityChange,
        onLoginClick = {
            focusManager.clearFocus()
            viewModel.login()
        },
        onForgotPasswordClick = onForgotPasswordClick,
        onFirstAccessClick = onFirstAccessClick
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onFirstAccessClick: () -> Unit
) {
    Scaffold(
        containerColor = VitalTraceWarmBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            LoginBrandHeader()
            LoginForm(
                uiState = uiState,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                onLoginClick = onLoginClick,
                onForgotPasswordClick = onForgotPasswordClick,
                onFirstAccessClick = onFirstAccessClick
            )
            LoginSupportNotice()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        LoginContent(
            uiState = LoginUiState(
                email = "ana.martinez@ejemplo.com",
                password = "12345678"
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibilityChange = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onFirstAccessClick = {}
        )
    }
}
