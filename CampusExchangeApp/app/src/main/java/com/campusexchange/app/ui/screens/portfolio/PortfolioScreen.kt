package com.campusexchange.app.ui.screens.portfolio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.ui.theme.*

@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel = hiltViewModel()) {
    // Re-fetch fresh data every time this tab becomes visible
    LaunchedEffect(Unit) { viewModel.load() }

    val uiState = viewModel.uiState.collectAsState().value

    val totalValue = uiState.holdings.sumOf { holding ->
        val stockPrice = uiState.allStocks.find { it.stockId == holding.stockId }?.price ?: 0.0
        holding.quantity * stockPrice
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .statusBarsPadding()
        ) {
            Text("PORTFOLIO", color = Soft, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
            Text("Your Holdings", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Primary)
        }

        // ── Portfolio value card ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp), clip = false)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Primary, Accent)))
                .padding(24.dp)
        ) {
            Column {
                Text("Total Portfolio Value", color = Soft, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text       = String.format("%.2f", totalValue),
                        color      = SurfaceWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 32.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("coins", color = Soft, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("${uiState.holdings.size} holdings", color = Soft, fontSize = 12.sp)
                    Text("  ·  ", color = SurfaceWhite.copy(alpha = 0.3f), fontSize = 12.sp)
                    Text("${uiState.pendingOrders.size} pending", color = Soft, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else {
            LazyColumn {
                if (uiState.holdings.isNotEmpty()) {
                    item {
                        Text(
                            text       = "Holdings",
                            color      = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 17.sp,
                            modifier   = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    items(uiState.holdings.filter { it.quantity > 0 }) { holding ->
                        val stock = uiState.allStocks.find { it.stockId == holding.stockId }
                        val value = holding.quantity * (stock?.price ?: 0.0)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                                .shadow(1.dp, RoundedCornerShape(14.dp), clip = false)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceLight)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = holding.stockId.take(3).uppercase(),
                                    color      = Accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 12.sp
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text       = stock?.name ?: holding.stockId,
                                    color      = Primary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 14.sp
                                )
                                Text(
                                    text     = "${holding.quantity} shares @ ${String.format("%.2f", stock?.price ?: 0.0)}",
                                    color    = Soft,
                                    fontSize = 11.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, null, tint = CoinGold, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text       = String.format("%.2f", value),
                                        color      = Primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 13.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector        = Icons.Default.ShowChart,
                                    contentDescription = null,
                                    tint               = Divider,
                                    modifier           = Modifier.size(56.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text("No holdings yet", color = Soft, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text      = "Buy stocks from the Market tab to start investing",
                                    color     = Soft,
                                    fontSize  = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }

                if (uiState.pendingOrders.isNotEmpty()) {
                    item {
                        Text(
                            text       = "Pending Orders",
                            color      = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 17.sp,
                            modifier   = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    items(uiState.pendingOrders) { order ->
                        val isBuy = order.type == "buy"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                                .shadow(1.dp, RoundedCornerShape(14.dp), clip = false)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isBuy) PositiveGreen.copy(0.1f) else NegativeRed.copy(0.1f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text       = order.type.uppercase(),
                                    color      = if (isBuy) PositiveGreen else NegativeRed,
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order.stockId, color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("${order.quantity} shares @ ${order.limitPrice}", color = Soft, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, null, tint = Soft, modifier = Modifier.size(13.dp))
                                Spacer(Modifier.width(3.dp))
                                Text("Pending", color = Soft, fontSize = 11.sp)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}
