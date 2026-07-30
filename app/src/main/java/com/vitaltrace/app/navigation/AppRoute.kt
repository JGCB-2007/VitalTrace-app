package com.vitaltrace.app.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Login : AppRoute("login")
    data object ForgotPassword : AppRoute("forgot_password")
    data object ResetPassword : AppRoute("reset_password")
    data object Home : AppRoute("home")
    data object Measurements : AppRoute("measurements")
    data object MeasurementForm : AppRoute("measurement_form")
    data object Appointments : AppRoute("appointments")
    data object Profile : AppRoute("profile")
    data object Relatives : AppRoute("relatives")
    data object Treatments : AppRoute("treatments")
    data object TreatmentDetail : AppRoute("treatments/{treatmentId}") {
        fun create(id: Long) = "treatments/$id"
    }
    data object ClinicalHistory : AppRoute("clinical_history")
}
