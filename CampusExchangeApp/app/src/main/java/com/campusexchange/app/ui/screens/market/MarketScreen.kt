package com.campusexchange.app.ui.screens.market

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.data.remote.dto.StockDto
import com.campusexchange.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(viewModel: MarketViewModel = hiltViewModel()) {
    val uiState           = viewModel.uiState.collectAsState().value
    val snackbarHostState  = remember { SnackbarHostState() }
    var selectedStock      by remember { mutableStateOf<StockWithChange?>(null) }

    LaunchedEffect(uiState.orderSuccess) {
        uiState.orderSuccess?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }
    LaunchedEffect(uiState.orderError) {
        uiState.orderError?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost   = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .statusBarsPadding()
            ) {
                Text("MARKET", color = Soft, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                Text(
                    text       = "Trade the things you love",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 24.sp,
                    color      = Primary,
                    lineHeight = 30.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Live pulse dot
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue  = 1f,
                        targetValue   = 0.25f,
                        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                        label         = "pa"
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(NeonGreen.copy(alpha = pulseAlpha))
                    )
                    Spacer(Modifier.width(5.dp))
                    Text("LIVE", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Prices refresh every 5s", color = Soft, fontSize = 11.sp)
                }
            }

            if (uiState.isLoading && uiState.stocks.isEmpty()) {
                repeat(5) { SkeletonStockRow() }
            } else {
                // Market summary strip
                if (uiState.stocks.isNotEmpty()) {
                    val gainers   = uiState.stocks.count { it.change24h > 0 }
                    val losers    = uiState.stocks.count { it.change24h < 0 }
                    val avgChange = uiState.stocks.map { it.change24h }.average()

                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MarketSummaryCard("Assets",  uiState.stocks.size.toString(), Accent,        Modifier.weight(1f))
                        MarketSummaryCard("Gainers", gainers.toString(),             PositiveGreen,  Modifier.weight(1f))
                        MarketSummaryCard("Losers",  losers.toString(),              NegativeRed,    Modifier.weight(1f))
                        MarketSummaryCard("Avg",     "${String.format("%.2f", avgChange)}%", Primary, Modifier.weight(1f))
                    }
                }

                // Column headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceLight)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text("ASSET",  color = Soft, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.8f), letterSpacing = 1.sp)
                    Text("PRICE",  color = Soft, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End, letterSpacing = 1.sp)
                    Text("CHANGE", color = Soft, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End, letterSpacing = 1.sp)
                    Text("ACTION", color = Soft, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, letterSpacing = 1.sp)
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.stocks) { swc ->
                        StockRow(stockWithChange = swc, onBuyClick = { selectedStock = swc })
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }

    selectedStock?.let { swc ->
        OrderBottomSheet(
            stock        = swc,
            onDismiss    = { selectedStock = null },
            onPlaceOrder = { qty, price, type ->
                viewModel.placeOrder(swc.stock.stockId, qty, price, type)
                selectedStock = null
            }
        )
    }
}

@Composable
fun MarketSummaryCard(title: String, value: String, valueColor: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(10.dp), clip = false)
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceWhite)
            .padding(10.dp)
    ) {
        Column {
            Text(title, color = Soft,       fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SkeletonStockRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceLight)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Divider))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(80.dp, 11.dp).clip(RoundedCornerShape(4.dp)).background(Divider))
            Spacer(modifier = Modifier.height(5.dp))
            Box(modifier = Modifier.size(50.dp, 9.dp).clip(RoundedCornerShape(4.dp)).background(Divider))
        }
    }
}

@Composable
fun StockRow(stockWithChange: StockWithChange, onBuyClick: () -> Unit) {
    val stock       = stockWithChange.stock
    val change      = stockWithChange.change24h
    val isPositive  = change >= 0
    val changeColor = if (isPositive) PositiveGreen else NegativeRed
    val changeBg    = if (isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

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
        // Ticker + Name
        Row(modifier = Modifier.weight(1.8f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceLight)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = stock.stockId.take(3).uppercase(),
                    color      = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text       = stock.name,
                    color      = Primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text("${stock.sharesct} shares", color = Soft, fontSize = 11.sp)
            }
        }

        // Price (animated)
        AnimatedContent(targetState = stock.price, label = "price") { price ->
            Text(
                text       = String.format("%.2f", price),
                color      = Primary,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                modifier   = Modifier.weight(1f),
                textAlign  = TextAlign.End
            )
        }

        // Change pill
        Box(
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.End)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(changeBg)
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text       = "${if (isPositive) "+" else ""}${String.format("%.2f", change)}%",
                    color      = changeColor,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Buy button
        Spacer(Modifier.width(8.dp))
        Button(
            onClick          = onBuyClick,
            colors           = ButtonDefaults.buttonColors(containerColor = Primary),
            shape            = RoundedCornerShape(10.dp),
            contentPadding   = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier         = Modifier.height(32.dp),
            elevation        = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text("Trade", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    stock: StockWithChange,
    onDismiss: () -> Unit,
    onPlaceOrder: (Int, Double, String) -> Unit
) {
    var quantity   by remember { mutableStateOf("1") }
    var limitPrice by remember { mutableStateOf(String.format("%.2f", stock.stock.price)) }
    var orderType  by remember { mutableStateOf("buy") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
            Text("Place Order", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Primary)
            Text(stock.stock.name, color = Soft, fontSize = 13.sp)
            Text(
                text     = "Current price: ${String.format("%.2f", stock.stock.price)} coins",
                color    = Accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(20.dp))

            // Buy / Sell toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceLight)
                    .padding(4.dp)
            ) {
                listOf("buy", "sell").forEach { type ->
                    val isSelected = orderType == type
                    val bgColor    = when {
                        isSelected && type == "buy"  -> PositiveGreen
                        isSelected && type == "sell" -> NegativeRed
                        else                         -> Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .clickable { orderType = type }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = type.uppercase(),
                            color      = if (isSelected) SurfaceWhite else Soft,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value           = quantity,
                onValueChange   = { quantity = it },
                label           = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true,
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Accent,
                    unfocusedBorderColor    = Divider,
                    focusedLabelColor       = Accent,
                    unfocusedLabelColor     = Soft,
                    focusedTextColor        = Primary,
                    unfocusedTextColor      = Primary
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value           = limitPrice,
                onValueChange   = { limitPrice = it },
                label           = { Text("Limit Price (coins)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true,
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Accent,
                    unfocusedBorderColor    = Divider,
                    focusedLabelColor       = Accent,
                    unfocusedLabelColor     = Soft,
                    focusedTextColor        = Primary,
                    unfocusedTextColor      = Primary
                )
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick  = {
                    val q = quantity.toIntOrNull() ?: 0
                    val p = limitPrice.toDoubleOrNull() ?: 0.0
                    if (q > 0 && p > 0.0) onPlaceOrder(q, p, orderType)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (orderType == "buy") PositiveGreen else NegativeRed
                ),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "PLACE ${orderType.uppercase()} ORDER",
                    color      = SurfaceWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
            }
        }
    }
}
