package com.campusexchange.app.ui.screens.leaderboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.data.remote.dto.LeaderboardEntryDto
import com.campusexchange.app.ui.theme.*

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .statusBarsPadding()
        ) {
            Text("LEADERBOARD", color = Soft, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
            Text("Top Earners", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Primary)
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else if (uiState.entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No leaderboard data yet", color = Soft, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn {
                if (uiState.entries.size >= 3) {
                    item {
                        PodiumSection(
                            first  = uiState.entries[0],
                            second = uiState.entries[1],
                            third  = uiState.entries[2]
                        )
                    }
                    itemsIndexed(uiState.entries.drop(3)) { index, entry ->
                        val rank = index + 4
                        AnimatedVisibility(
                            visible = true,
                            enter   = slideInHorizontally(
                                initialOffsetX = { 160 },
                                animationSpec  = tween(280 + index * 40)
                            ) + fadeIn()
                        ) {
                            LeaderboardRow(rank = rank, entry = entry)
                        }
                    }
                } else {
                    itemsIndexed(uiState.entries) { index, entry ->
                        val rank = index + 1
                        AnimatedVisibility(
                            visible = true,
                            enter   = slideInHorizontally(
                                initialOffsetX = { 160 },
                                animationSpec  = tween(280 + index * 40)
                            ) + fadeIn()
                        ) {
                            LeaderboardRow(rank = rank, entry = entry)
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun PodiumSection(
    first: LeaderboardEntryDto,
    second: LeaderboardEntryDto,
    third: LeaderboardEntryDto
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(3.dp, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(listOf(Primary, Accent))
            )
            .padding(24.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            PodiumItem(rank = 3, entry = third,  height = 76.dp)
            PodiumItem(rank = 1, entry = first,  height = 112.dp, showCrown = true)
            PodiumItem(rank = 2, entry = second, height = 92.dp)
        }
    }
}

@Composable
fun PodiumItem(
    rank: Int,
    entry: LeaderboardEntryDto,
    height: Dp,
    showCrown: Boolean = false
) {
    val scale = if (rank == 1) {
        val t = rememberInfiniteTransition(label = "pulse")
        val s by t.animateFloat(
            initialValue = 1f,
            targetValue  = 1.04f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
            label        = "s"
        )
        s
    } else 1f

    val rankColor = when (rank) {
        1    -> CoinGold
        2    -> Color(0xFFB0BEC5)  // silver
        else -> Color(0xFFCD7F32)  // bronze
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.scale(scale)
    ) {
        if (showCrown) {
            Icon(
                imageVector        = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint               = CoinGold,
                modifier           = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(2.dp))
        }
        Box(
            modifier = Modifier
                .size(height)
                .clip(CircleShape)
                .background(SurfaceWhite.copy(alpha = 0.18f))
                .border(2.dp, rankColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = entry.username.take(1).uppercase(),
                fontSize   = (height.value * 0.34).sp,
                fontWeight = FontWeight.ExtraBold,
                color      = SurfaceWhite
            )
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(rankColor)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("#$rank", color = Primary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text("@${entry.username}", color = SurfaceWhite, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        Text(
            text       = "${String.format("%.0f", entry.campusCoins)} coins",
            color      = rankColor,
            fontWeight = FontWeight.Bold,
            fontSize   = 12.sp
        )
    }
}

@Composable
fun LeaderboardRow(rank: Int, entry: LeaderboardEntryDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .shadow(1.dp, RoundedCornerShape(14.dp), clip = false)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = "#$rank",
            color      = Soft,
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp,
            modifier   = Modifier.width(36.dp)
        )
        Box(
            modifier         = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = entry.username.take(1).uppercase(),
                color      = Accent,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text       = "@${entry.username}",
            color      = Primary,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 14.sp,
            modifier   = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint               = CoinGold,
                modifier           = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text       = String.format("%.0f", entry.campusCoins),
                color      = Primary,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp
            )
        }
    }
}
