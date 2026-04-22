package com.campusexchange.app.ui.screens.betting

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.data.remote.dto.BetDto
import com.campusexchange.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BettingScreen(viewModel: BettingViewModel = hiltViewModel()) {
    // Re-fetch fresh data every time this tab becomes visible
    LaunchedEffect(Unit) { viewModel.loadData() }

    val uiState           = viewModel.uiState.collectAsState().value
    val snackbarHostState  = remember { SnackbarHostState() }
    var selectedBet        by remember { mutableStateOf<BetDto?>(null) }
    var showMyBets         by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
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
                Text("BETS", color = Soft, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                Text(
                    text       = "Prediction Market",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 24.sp,
                    color      = Primary
                )
                Text("Bet coins on campus events", color = Soft, fontSize = 12.sp)
            }

            // ── Tab toggle ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceLight)
                    .padding(4.dp)
            ) {
                listOf("All Bets", "My Bets").forEachIndexed { index, label ->
                    val isSelected = showMyBets == (index == 1)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Primary else Color.Transparent)
                            .clickable { showMyBets = index == 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = label,
                            color      = if (isSelected) SurfaceWhite else Soft,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            } else if (!showMyBets) {
                LazyColumn {
                    items(uiState.allBets) { bet ->
                        BetCard(bet = bet, onPlaceBet = { selectedBet = bet })
                    }
                    if (uiState.allBets.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No active bets right now", color = Soft, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            } else {
                LazyColumn {
                    items(uiState.myBets) { enrolled ->
                        val isOpen = enrolled.status == "open"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .shadow(2.dp, RoundedCornerShape(16.dp), clip = false)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceWhite)
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text       = if (isOpen) "Open" else "Closed",
                                            color      = if (isOpen) PositiveGreen else NegativeRed,
                                            fontSize   = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text       = "${enrolled.myCoins.toInt()} coins wagered",
                                        color      = CoinGold,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 12.sp
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(enrolled.question, color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Your pick: ", color = Soft, fontSize = 13.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (enrolled.myResponse.lowercase() == "yes")
                                                    Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text       = enrolled.myResponse.uppercase(),
                                            color      = if (enrolled.myResponse.lowercase() == "yes") PositiveGreen else NegativeRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize   = 12.sp
                                        )
                                    }
                                }
                                if (!isOpen) {
                                    Spacer(Modifier.height(6.dp))
                                    val won = enrolled.result?.lowercase() == enrolled.myResponse.lowercase()
                                    Text(
                                        text       = if (won) "You Won" else "You Lost",
                                        color      = if (won) PositiveGreen else NegativeRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 13.sp
                                    )
                                }
                            }
                        }
                    }
                    if (uiState.myBets.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("You haven't placed any bets yet", color = Soft, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }

    selectedBet?.let { bet ->
        PlaceBetSheet(
            bet       = bet,
            onDismiss = { selectedBet = null },
            onConfirm = { response, coins ->
                viewModel.enrollBet(bet.betId, response, coins)
                selectedBet = null
            }
        )
    }
}

// ── Bet Card ─────────────────────────────────────────────────────────────────

@Composable
fun BetCard(bet: BetDto, onPlaceBet: () -> Unit) {
    val isClosed   = bet.status == "closed"
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val deadline   = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        dateFormat.format(sdf.parse(bet.resultTime) ?: Date())
    } catch (e: Exception) { bet.resultTime }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp), clip = false)
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceWhite)
            .padding(20.dp)
    ) {
        Column {
            // Status + pool row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isClosed) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = bet.status.uppercase(),
                        color      = if (isClosed) NegativeRed else PositiveGreen,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint               = CoinGold,
                        modifier           = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text       = "${bet.totalPool.toInt()} coin pool",
                        color      = CoinGold,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text       = bet.question,
                color      = Primary,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                lineHeight = 22.sp
            )
            bet.description?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, color = Soft, fontSize = 13.sp, lineHeight = 19.sp)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, null, tint = Soft, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${bet.totalEnrolled} enrolled", color = Soft, fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = Soft, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(deadline, color = Soft, fontSize = 11.sp)
                    }
                }
                if (!isClosed) {
                    Button(
                        onClick  = onPlaceBet,
                        colors   = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape    = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Place Bet", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ── Place Bet Sheet ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceBetSheet(bet: BetDto, onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var selectedOption by remember { mutableStateOf("yes") }
    var coinsInput     by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
            Text("Place Bet", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Primary)
            Spacer(Modifier.height(4.dp))
            Text(bet.question, color = Soft, fontSize = 13.sp, lineHeight = 20.sp)

            Spacer(Modifier.height(20.dp))

            // Pool info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceLight)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Pool", color = Soft, fontSize = 11.sp)
                    Text("${bet.totalPool.toInt()} coins", color = Primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Participants", color = Soft, fontSize = 11.sp)
                    Text("${bet.totalEnrolled}", color = Primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Your Prediction", color = Soft, fontSize = 12.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))

            // Yes / No pill toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceLight)
                    .padding(4.dp)
            ) {
                listOf("yes", "no").forEach { option ->
                    val isSelected  = selectedOption == option
                    val selectedBg  = if (option == "yes") PositiveGreen else NegativeRed
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) selectedBg else Color.Transparent)
                            .border(
                                width  = if (!isSelected) 1.dp else 0.dp,
                                color  = if (!isSelected) Divider else Color.Transparent,
                                shape  = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedOption = option }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector        = if (option == "yes") Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint               = if (isSelected) SurfaceWhite else Soft,
                                modifier           = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text       = option.uppercase(),
                                color      = if (isSelected) SurfaceWhite else Soft,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp
                            )
                        }
                    }
                    if (option == "yes") Spacer(Modifier.width(6.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value         = coinsInput,
                onValueChange = { coinsInput = it.filter { c -> c.isDigit() || c == '.' } },
                label         = { Text("Coins to Wager") },
                leadingIcon   = {
                    Icon(Icons.Default.MonetizationOn, null, tint = CoinGold, modifier = Modifier.size(20.dp))
                },
                modifier        = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                    val coins = coinsInput.toDoubleOrNull() ?: return@Button
                    if (coins > 0) onConfirm(selectedOption, coins)
                },
                enabled  = coinsInput.isNotBlank() && (coinsInput.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption == "yes") PositiveGreen else NegativeRed
                ),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text       = "Confirm ${selectedOption.uppercase()} — ${coinsInput.ifBlank { "?" }} coins",
                    color      = SurfaceWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp
                )
            }
        }
    }
}
