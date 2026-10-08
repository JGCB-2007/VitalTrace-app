package com.vitaltrace.app

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.vitaltrace.app.feature.auth.presentation.LoginUiState
import com.vitaltrace.app.feature.auth.presentation.components.LoginForm
import com.vitaltrace.app.feature.home.presentation.MeasurementTrendPoint
import com.vitaltrace.app.feature.home.presentation.RecentMeasurementUiModel
import com.vitaltrace.app.feature.home.presentation.components.RecentPressureCard
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import org.junit.Rule
import org.junit.Test

class AccessibilitySmokeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun login_exposes_primary_actions_and_remember_control() {
        composeRule.setContent {
            VitalTraceTheme {
                LoginForm(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onPasswordVisibilityChange = {},
                    onRememberMeChange = {},
                    onLoginClick = {},
                    onForgotPasswordClick = {},
                    onFirstAccessClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Iniciar sesión").assertHasClickAction()
        composeRule.onNodeWithText("Recordarme").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Mostrar contraseña").assertHasClickAction()
    }

    @Test
    fun latest_measurement_announces_real_type_and_values() {
        composeRule.setContent {
            VitalTraceTheme {
                RecentPressureCard(
                    measurement = RecentMeasurementUiModel(
                        typeName = "Glucosa en sangre",
                        value = "104",
                        unit = "mg/dL",
                        date = "2026-09-24",
                        trendPoints = listOf(MeasurementTrendPoint(0.65f, "104", "09/24"))
                    ),
                    onHistoryClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Glucosa en sangre").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("09/24: 104 mg/dL").assertIsDisplayed()
    }
}
