package com.vitaltrace.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitaltrace.app.feature.auth.presentation.LoginScreen
import com.vitaltrace.app.feature.auth.presentation.recovery.ForgotPasswordScreen
import com.vitaltrace.app.feature.auth.presentation.recovery.ResetPasswordScreen
import com.vitaltrace.app.feature.appointments.presentation.AppointmentsScreen
import com.vitaltrace.app.feature.clinicalhistory.presentation.ClinicalHistoryScreen
import com.vitaltrace.app.feature.diagnosiseducation.presentation.DiagnosisEducationScreen
import com.vitaltrace.app.feature.home.presentation.HomeScreen
import com.vitaltrace.app.feature.measurements.presentation.MeasurementsScreen
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementFormScreen
import com.vitaltrace.app.feature.notifications.presentation.NotificationsScreen
import com.vitaltrace.app.feature.profile.presentation.ProfileScreen
import com.vitaltrace.app.feature.relatives.presentation.RelativesScreen
import com.vitaltrace.app.feature.splash.presentation.SplashScreen
import com.vitaltrace.app.feature.treatments.presentation.TreatmentDetailScreen
import com.vitaltrace.app.feature.treatments.presentation.TreatmentsScreen
import com.vitaltrace.app.feature.treatments.presentation.TreatmentsViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route
    ) {
        composable(AppRoute.Notifications.route) {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNotificationAction = { actionRoute, relatedId ->
                    when (actionRoute?.lowercase()) {
                        "appointments" -> {
                            val destination = relatedId?.let(AppRoute.AppointmentDetail::create)
                                ?: AppRoute.Appointments.route
                            navController.navigate(destination) { launchSingleTop = true }
                        }
                        "measurements" -> {
                            navController.navigate(AppRoute.Measurements.route) {
                                launchSingleTop = true
                            }
                        }
                        "treatments" -> {
                            navController.navigate(AppRoute.Treatments.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

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
                },
                onForgotPasswordClick = {
                    navController.navigate(AppRoute.ForgotPassword.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.Home.route) {
            HomeScreen(
                onNotificationsClick = {
                    navController.navigate(AppRoute.Notifications.route) {
                        launchSingleTop = true
                    }
                },
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
                },
                onRegisterMeasurementClick = {
                    navController.navigate(AppRoute.MeasurementForm.route) {
                        launchSingleTop = true
                    }
                },
                onAppointmentsClick = {
                    navController.navigate(AppRoute.Appointments.route) {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppRoute.Profile.route) {
                        launchSingleTop = true
                    }
                },
                onAppointmentDetailClick = { appointmentId ->
                    navController.navigate(AppRoute.AppointmentDetail.create(appointmentId)) {
                        launchSingleTop = true
                    }
                },
                onPressureHistoryClick = {
                    navController.navigate(AppRoute.Measurements.route) { launchSingleTop = true }
                }
            )
        }

        composable(AppRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onEnterToken = {
                    navController.navigate(AppRoute.ResetPassword.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.ResetPassword.route) {
            ResetPasswordScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Login.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.Relatives.route) {
            RelativesScreen(
                onNavigateBack = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onMeasurementsClick = {
                    navController.navigate(AppRoute.Measurements.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onAppointmentsClick = {
                    navController.navigate(AppRoute.Appointments.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onProfileClick = { navController.popBackStack() }
            )
        }

        composable(AppRoute.ClinicalHistory.route) {
            ClinicalHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onEducationClick = { cieCode, diagnosisName ->
                    navController.navigate(AppRoute.DiagnosisEducation.create(cieCode, diagnosisName))
                }
            )
        }

        composable(
            route = AppRoute.DiagnosisEducation.route,
            arguments = listOf(
                navArgument("cieCode") { type = NavType.StringType },
                navArgument("diagnosisName") { type = NavType.StringType }
            )
        ) {
            DiagnosisEducationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(AppRoute.Treatments.route) {
            TreatmentsScreen(
                onTreatmentClick = { id ->
                    navController.navigate(AppRoute.TreatmentDetail.create(id))
                },
                onHomeClick = { navController.popBackStack() },
                onMeasurementsClick = {
                    navController.navigate(AppRoute.Measurements.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onAppointmentsClick = {
                    navController.navigate(AppRoute.Appointments.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppRoute.Profile.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppRoute.TreatmentDetail.route,
            arguments = listOf(navArgument("treatmentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val treatmentsEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppRoute.Treatments.route)
            }
            val viewModel: TreatmentsViewModel = hiltViewModel(treatmentsEntry)
            TreatmentDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(AppRoute.Measurements.route) {
            MeasurementsScreen(
                onHomeClick = {
                    navController.popBackStack()
                },
                onAddMeasurementClick = {
                    navController.navigate(AppRoute.MeasurementForm.route) {
                        launchSingleTop = true
                    }
                },
                onAppointmentsClick = {
                    navController.navigate(AppRoute.Appointments.route) {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppRoute.Profile.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.Appointments.route) {
            AppointmentsScreen(
                onHomeClick = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onMeasurementsClick = {
                    navController.navigate(AppRoute.Measurements.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppRoute.Profile.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppRoute.AppointmentDetail.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.LongType })
        ) { backStackEntry ->
            AppointmentsScreen(
                onHomeClick = { navController.navigate(AppRoute.Home.route) { popUpTo(AppRoute.Home.route) } },
                onMeasurementsClick = { navController.navigate(AppRoute.Measurements.route) },
                onProfileClick = { navController.navigate(AppRoute.Profile.route) },
                initialAppointmentId = backStackEntry.arguments?.getLong("appointmentId"),
                onInitialDetailDismiss = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Profile.route) {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onMeasurementsClick = {
                    navController.navigate(AppRoute.Measurements.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onAppointmentsClick = {
                    navController.navigate(AppRoute.Appointments.route) {
                        popUpTo(AppRoute.Home.route)
                        launchSingleTop = true
                    }
                },
                onRelativesClick = {
                    navController.navigate(AppRoute.Relatives.route) {
                        launchSingleTop = true
                    }
                },
                onClinicalHistoryClick = {
                    navController.navigate(AppRoute.ClinicalHistory.route) {
                        launchSingleTop = true
                    }
                },
                onTreatmentsClick = {
                    navController.navigate(AppRoute.Treatments.route) { launchSingleTop = true }
                }
            )
        }

        composable(AppRoute.MeasurementForm.route) {
            MeasurementFormScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMeasurementSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}

