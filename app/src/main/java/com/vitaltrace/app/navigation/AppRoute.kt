package com.vitaltrace.app.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Login : AppRoute("login")
    data object Home : AppRoute("home")
    data object Measurements : AppRoute("measurements")
    data object MeasurementForm : AppRoute("measurement_form")
}
