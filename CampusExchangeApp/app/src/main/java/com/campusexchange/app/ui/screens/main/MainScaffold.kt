package com.campusexchange.app.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.campusexchange.app.ui.screens.betting.BettingScreen
import com.campusexchange.app.ui.screens.home.HomeScreen
import com.campusexchange.app.ui.screens.leaderboard.LeaderboardScreen
import com.campusexchange.app.ui.screens.market.MarketScreen
import com.campusexchange.app.ui.screens.portfolio.PortfolioScreen
import com.campusexchange.app.ui.screens.profile.ProfileScreen
import com.campusexchange.app.ui.theme.*

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun MainScaffold(navController: NavController) {
    val navItems = listOf(
        BottomNavItem("Home",      Icons.Default.Home,           "home"),
        BottomNavItem("Market",    Icons.Default.ShowChart,      "market"),
        BottomNavItem("Bets",      Icons.Default.Casino,         "bets"),
        BottomNavItem("Ranks",     Icons.Default.EmojiEvents,    "leaderboard"),
        BottomNavItem("Portfolio", Icons.Default.AccountBalance, "portfolio"),
        BottomNavItem("Profile",   Icons.Default.Person,         "profile")
    )
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            Surface(
                modifier  = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .shadow(
                        elevation       = 8.dp,
                        shape           = RoundedCornerShape(24.dp),
                        ambientColor    = Primary.copy(alpha = 0.08f),
                        spotColor       = Primary.copy(alpha = 0.12f)
                    )
                    .clip(RoundedCornerShape(24.dp)),
                color = SurfaceWhite,
                tonalElevation = 0.dp
            ) {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    modifier       = Modifier.height(64.dp)
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick  = { selectedIndex = index },
                            icon = {
                                Icon(
                                    imageVector        = item.icon,
                                    contentDescription = item.label,
                                    modifier           = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(item.label, fontSize = 10.sp)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = Primary,
                                selectedTextColor   = Primary,
                                unselectedIconColor = Soft,
                                unselectedTextColor = Soft,
                                indicatorColor      = SurfaceLight
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> HomeScreen(
                    onNavigateToMarket       = { selectedIndex = 1 },
                    onNavigateToBets         = { selectedIndex = 2 },
                    onNavigateToLeaderboard  = { selectedIndex = 3 },
                    onNavigateToPortfolio    = { selectedIndex = 4 },
                    onNavigateToProfile      = { selectedIndex = 5 }
                )
                1 -> MarketScreen()
                2 -> BettingScreen()
                3 -> LeaderboardScreen()
                4 -> PortfolioScreen()
                5 -> ProfileScreen(onLogout = {
                    navController.navigate("landing") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                })
            }
        }
    }
}
