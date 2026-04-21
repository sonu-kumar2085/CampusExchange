package com.campusexchange.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToLanding: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)
    var isReady by remember { mutableStateOf(false) }

    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        launch {
            alpha.animateTo(1f, animationSpec = tween(600))
        }
        delay(1800)
        isReady = true
    }

    LaunchedEffect(isReady, isLoggedIn) {
        if (isReady) {
            if (isLoggedIn) onNavigateToDashboard() else onNavigateToLanding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            // Logo coin icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(NeonGreen, shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🪙",
                    fontSize = 40.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "CampusExchange",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = "Walk. Earn. Invest.",
                fontSize = 14.sp,
                color = SoftGreen,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
