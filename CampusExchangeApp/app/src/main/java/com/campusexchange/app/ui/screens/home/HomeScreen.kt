package com.campusexchange.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToMarket: () -> Unit = {},
    onNavigateToBets: () -> Unit = {},
    onNavigateToPortfolio: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Re-fetch fresh data every time this tab becomes visible
    LaunchedEffect(Unit) { viewModel.load() }

    val uiState by viewModel.uiState.collectAsState()

    val coins       = uiState.wallet?.campusCoins ?: 0.0
    val totalSteps  = uiState.remoteSteps?.stepsCount ?: 0
    val todaySteps  = uiState.localSteps?.stepsCount ?: 0
    val dailyGoal   = 10_000
    val stepProg    = (todaySteps.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)

    // Animated float for each stat card counter
    val animatedCoins by animateFloatAsState(
        targetValue  = coins.toFloat(),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label        = "coins"
    )
    val animatedTotal by animateFloatAsState(
        targetValue  = totalSteps.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label        = "total_steps"
    )
    val animatedToday by animateFloatAsState(
        targetValue  = todaySteps.toFloat(),
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label        = "today_steps"
    )
    val animatedProgress by animateFloatAsState(
        targetValue  = stepProg,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label        = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar with app title ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text       = "CampusExchange",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 22.sp,
                    color      = Primary
                )
                Text(
                    text     = "Your campus financial hub",
                    fontSize = 12.sp,
                    color    = Soft
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Accent)
                    .clickable { onNavigateToProfile() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint               = SurfaceWhite,
                    modifier           = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Stats Section Label ──────────────────────────────────────────────
        Text(
            text       = "Overview",
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = Primary,
            modifier   = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Three stats cards ────────────────────────────────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier    = Modifier.weight(1f),
                label       = "Total Steps",
                value       = animatedTotal.toInt().toString(),
                icon        = Icons.Default.DirectionsWalk,
                iconTint    = Accent,
                iconBg      = SurfaceLight,
                accentColor = Accent
            )
            StatCard(
                modifier    = Modifier.weight(1f),
                label       = "Today",
                value       = animatedToday.toInt().toString(),
                icon        = Icons.Default.Favorite,
                iconTint    = NeonGreen,
                iconBg      = Color(0xFFE6F7EE),
                accentColor = NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Coins card — full width
        CoinCard(
            coins    = animatedCoins,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Daily goal ring card ─────────────────────────────────────────────
        DailyGoalCard(
            todaySteps      = todaySteps,
            dailyGoal       = dailyGoal,
            animatedProgress = animatedProgress,
            modifier        = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Quick actions ────────────────────────────────────────────────────
        Text(
            text       = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = Primary,
            modifier   = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier  = Modifier.weight(1f),
                label     = "Market",
                icon      = Icons.Default.ShowChart,
                onClick   = onNavigateToMarket
            )
            QuickActionCard(
                modifier  = Modifier.weight(1f),
                label     = "Bets",
                icon      = Icons.Default.Casino,
                onClick   = onNavigateToBets
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier  = Modifier.weight(1f),
                label     = "Portfolio",
                icon      = Icons.Default.AccountBalance,
                onClick   = onNavigateToPortfolio
            )
            QuickActionCard(
                modifier  = Modifier.weight(1f),
                label     = "Ranks",
                icon      = Icons.Default.EmojiEvents,
                onClick   = onNavigateToLeaderboard
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Stat Card ─────────────────────────────────────────────────────────────────

@Composable
private fun StatCard(
    modifier: Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    accentColor: Color
) {
    Box(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = iconTint,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text       = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 22.sp,
                color      = Primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = label,
                fontSize = 11.sp,
                color    = Soft
            )
        }
    }
}

// ── Coin Card ─────────────────────────────────────────────────────────────────

@Composable
private fun CoinCard(coins: Float, modifier: Modifier) {
    Box(
        modifier = modifier
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Primary, Accent)
                )
            )
            .padding(24.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text     = "Campus Coins",
                    color    = Soft,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = String.format("%.0f", coins),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 34.sp,
                    color      = SurfaceWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "10 steps = 1 coin",
                    color    = Soft,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SurfaceWhite.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint               = CoinGold,
                    modifier           = Modifier.size(30.dp)
                )
            }
        }
    }
}

// ── Daily Goal Card ───────────────────────────────────────────────────────────

@Composable
private fun DailyGoalCard(
    todaySteps: Int,
    dailyGoal: Int,
    animatedProgress: Float,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(80.dp)) {
                    val stroke = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(
                        color      = Divider,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter  = false,
                        style      = stroke
                    )
                    drawArc(
                        color      = android.graphics.Color.parseColor("#00875A").let {
                            Color(it)
                        },
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter  = false,
                        style      = stroke
                    )
                }
                Text(
                    text       = "${(animatedProgress * 100).toInt()}%",
                    color      = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text       = "Daily Step Goal",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp,
                    color      = Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "$todaySteps / $dailyGoal steps",
                    fontSize = 13.sp,
                    color    = Soft
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text       = "${todaySteps / 10} coins earned today",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = NeonGreen
                )
            }
        }
    }
}

// ── Quick Action Card ─────────────────────────────────────────────────────────

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), clip = false)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = label,
                    tint               = Accent,
                    modifier           = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = label,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp,
                color      = Primary,
                textAlign  = TextAlign.Center
            )
        }
    }
}
