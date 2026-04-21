package com.campusexchange.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Dashboard : Screen("dashboard")
    object Market : Screen("market")
    object Betting : Screen("betting")
    object Leaderboard : Screen("leaderboard")
    object Portfolio : Screen("portfolio")
    object Profile : Screen("profile")
}
