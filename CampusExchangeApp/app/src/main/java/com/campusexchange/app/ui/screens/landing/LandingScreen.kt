package com.campusexchange.app.ui.screens.landing

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusexchange.app.ui.theme.*

@Composable
fun LandingScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Subtle floating animation for hero card
    val infiniteTransition = rememberInfiniteTransition(label = "hero")
    val yOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue  = 4f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ── Top bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint               = SurfaceWhite,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text       = "CampusExchange",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        color      = Primary
                    )
                }
                TextButton(onClick = onLogin) {
                    Text(
                        text       = "Sign In",
                        color      = Accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ── Beta badge ───────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(SurfaceLight, RoundedCornerShape(50.dp))
                        .border(1.dp, Divider, RoundedCornerShape(50.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text       = "Now in beta — earn coins from every step",
                            color      = Accent,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Hero headline ─────────────────────────────────────────────────
            Column(
                modifier              = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment   = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color      = Primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 38.sp
                            )
                        ) { append("Turn your steps\ninto ") }
                        withStyle(
                            SpanStyle(
                                color      = NeonGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 38.sp
                            )
                        ) { append("wealth") }
                        withStyle(
                            SpanStyle(
                                color      = Primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 38.sp
                            )
                        ) { append(".") }
                    },
                    textAlign  = TextAlign.Center,
                    lineHeight = 46.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text       = "Walk. Earn. Invest. Build a portfolio with every step you take — powered by a simulated market built for campus movers.",
                    color      = Soft,
                    fontSize   = 15.sp,
                    textAlign  = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Floating hero card ────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .offset(y = yOffset.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation    = 12.dp,
                            shape        = RoundedCornerShape(24.dp),
                            ambientColor = Primary.copy(alpha = 0.10f),
                            spotColor    = Primary.copy(alpha = 0.16f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Primary, Accent)
                            )
                        )
                        .padding(28.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HeroStatItem(
                            icon     = Icons.Default.DirectionsWalk,
                            top      = "10 Steps",
                            bottom   = "= 1 Coin"
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .background(SurfaceWhite.copy(alpha = 0.15f))
                        )
                        HeroStatItem(
                            icon     = Icons.Default.ShowChart,
                            top      = "Live Market",
                            bottom   = "Simulated"
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .background(SurfaceWhite.copy(alpha = 0.15f))
                        )
                        HeroStatItem(
                            icon     = Icons.Default.Casino,
                            top      = "Bet Events",
                            bottom   = "Win Big"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── CTA buttons ───────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape  = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text       = "Get Started",
                        color      = SurfaceWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    border = BorderStroke(1.5.dp, Accent),
                    shape  = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text       = "Sign In",
                        color      = Accent,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text      = "No credit card required · 10 steps = 1 coin",
                    color     = Soft,
                    fontSize  = 12.sp,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // ── Why section ───────────────────────────────────────────────────
            Text(
                text       = "Why CampusExchange?",
                modifier   = Modifier.padding(horizontal = 24.dp),
                fontWeight = FontWeight.Bold,
                fontSize   = 22.sp,
                color      = Primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                FeatureCard(
                    icon  = Icons.Default.DirectionsWalk,
                    title = "Earn from Every Step",
                    desc  = "Your pedometer tracks steps all day. Every 10 steps = 1 CampusCoin automatically credited every midnight."
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureCard(
                    icon  = Icons.Default.ShowChart,
                    title = "Simulated Stock Market",
                    desc  = "Trade campus-themed stocks. Hackathon Points, Coding Skill Index, Fitness Index and more."
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureCard(
                    icon  = Icons.Default.Casino,
                    title = "Prediction Betting",
                    desc  = "Bet coins on real campus events. Winners split the prize pool proportionally."
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureCard(
                    icon  = Icons.Default.EmojiEvents,
                    title = "Global Leaderboard",
                    desc  = "Compete with your campus peers. Climb the leaderboard by smart trading and walking more."
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text      = "2025 CampusExchange",
                color     = Soft.copy(alpha = 0.6f),
                fontSize  = 11.sp,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HeroStatItem(icon: ImageVector, top: String, bottom: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = SurfaceWhite,
            modifier           = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(top,    color = SurfaceWhite, fontWeight = FontWeight.Bold,   fontSize = 12.sp)
        Text(bottom, color = Soft,         fontWeight = FontWeight.Normal,  fontSize = 11.sp)
    }
}

@Composable
private fun FeatureCard(icon: ImageVector, title: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier         = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = Accent,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text       = title,
                    color      = Primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = desc,
                    color      = Soft,
                    fontSize   = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
