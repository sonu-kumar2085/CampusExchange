package com.campusexchange.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailMode     by remember { mutableStateOf(true) }

    val uiState          = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess()
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost   = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header band ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Primary, Accent))
                    )
                    .statusBarsPadding()
                    .padding(top = 8.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Back button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector        = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint               = SurfaceWhite
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceWhite.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Lock,
                            contentDescription = null,
                            tint               = SurfaceWhite,
                            modifier           = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text       = "Welcome Back",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 24.sp,
                        color      = SurfaceWhite
                    )
                    Text(
                        text     = "Sign in to your CampusExchange account",
                        color    = Soft,
                        fontSize = 13.sp
                    )
                }
            }

            // ── Form card ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-20).dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), clip = false)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceWhite)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text       = "Sign In",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 22.sp,
                        color      = Primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Email / Username toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Use", color = Soft, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = isEmailMode,
                            onClick  = { isEmailMode = true; emailOrUsername = "" },
                            label    = { Text("Email") },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor     = SurfaceWhite
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = !isEmailMode,
                            onClick  = { isEmailMode = false; emailOrUsername = "" },
                            label    = { Text("Username") },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor     = SurfaceWhite
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value         = emailOrUsername,
                        onValueChange = { emailOrUsername = it },
                        label         = { Text(if (isEmailMode) "Email" else "Username") },
                        leadingIcon   = {
                            Icon(
                                if (isEmailMode) Icons.Default.Email else Icons.Default.Person,
                                contentDescription = null,
                                tint               = Accent
                            )
                        },
                        modifier        = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isEmailMode) KeyboardType.Email else KeyboardType.Text
                        ),
                        singleLine = true,
                        colors     = lightAuthFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = { Text("Password") },
                        leadingIcon   = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Accent)
                        },
                        trailingIcon  = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint               = Soft
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        modifier        = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine      = true,
                        colors          = lightAuthFieldColors()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick  = { viewModel.login(emailOrUsername, password, isEmailMode) },
                        enabled  = emailOrUsername.isNotBlank() && password.isNotBlank() && !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors    = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape     = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color       = SurfaceWhite,
                                modifier    = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Sign In", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have an account? ", color = Soft, fontSize = 13.sp)
                        Text(
                            text       = "Sign Up",
                            color      = Accent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp,
                            modifier   = Modifier.clickable(onClick = onNavigateToSignup)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun authFieldColors() = lightAuthFieldColors()

@Composable
fun lightAuthFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = Accent,
    unfocusedBorderColor    = Divider,
    focusedLabelColor       = Accent,
    unfocusedLabelColor     = Soft,
    focusedTextColor        = Primary,
    unfocusedTextColor      = Primary,
    cursorColor             = Accent,
    focusedContainerColor   = SurfaceWhite,
    unfocusedContainerColor = SurfaceWhite
)
