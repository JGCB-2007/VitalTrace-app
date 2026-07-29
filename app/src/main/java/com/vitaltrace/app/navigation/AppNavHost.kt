package com.vitaltrace.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitaltrace.app.feature.auth.presentation.LoginScreen
import com.vitaltrace.app.feature.home.presentation.HomeScreen
import com.vitaltrace.app.feature.measurements.presentation.MeasurementsScreen
import com.vitaltrace.app.feature.splash.presentation.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route
    ) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Splash.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoute.Home.route) {
            HomeScreen(
                onLogoutSuccess = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onMeasurementsClick = {
                    navController.navigate(AppRoute.Measurements.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.Measurements.route) {
            MeasurementsScreen(
                onHomeClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
