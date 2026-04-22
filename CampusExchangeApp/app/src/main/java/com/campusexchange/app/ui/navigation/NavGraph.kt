package com.campusexchange.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusexchange.app.ui.screens.auth.LoginScreen
import com.campusexchange.app.ui.screens.auth.SignupScreen
import com.campusexchange.app.ui.screens.landing.LandingScreen
import com.campusexchange.app.ui.screens.main.MainScaffold
import com.campusexchange.app.ui.screens.splash.SplashScreen

@Composable
fun CampusExchangeNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToLanding = {
                    navController.navigate("landing") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("landing") {
            LandingScreen(
                onGetStarted = { navController.navigate("signup") },
                onLogin      = { navController.navigate("login") }
            )
        }

        composable("signup") {
            SignupScreen(
                onSignupSuccess   = {
                    navController.navigate("dashboard") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate("login") },
                onBack            = { navController.popBackStack() }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess     = {
                    navController.navigate("dashboard") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate("signup") },
                onBack             = { navController.popBackStack() }
            )
        }

        composable("dashboard") {
            MainScaffold(navController = navController)
        }
    }
}
