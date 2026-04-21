package com.campusexchange.app.ui.screens.auth

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
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var username        by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState           = viewModel.uiState.collectAsState().value
    val snackbarHostState  = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSignupSuccess()
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
                    .background(Brush.verticalGradient(listOf(Primary, Accent)))
                    .statusBarsPadding()
                    .padding(top = 8.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            imageVector        = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint               = SurfaceWhite,
                            modifier           = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text       = "Create Account",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 24.sp,
                        color      = SurfaceWhite
                    )
                    Text(
                        text     = "Start earning coins from your steps",
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
                        text       = "Sign Up",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 22.sp,
                        color      = Primary
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value         = fullName,
                        onValueChange = { fullName = it },
                        label         = { Text("Full Name") },
                        leadingIcon   = { Icon(Icons.Default.Badge, contentDescription = null, tint = Accent) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        colors        = lightAuthFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Email") },
                        leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null, tint = Accent) },
                        modifier        = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine      = true,
                        colors          = lightAuthFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = username,
                        onValueChange = { username = it.lowercase().replace(" ", "") },
                        label         = { Text("Username") },
                        leadingIcon   = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Accent) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        colors        = lightAuthFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = { Text("Password") },
                        leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null, tint = Accent) },
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
                        onClick  = { viewModel.register(fullName, email, username, password) },
                        enabled  = listOf(fullName, email, username, password).all { it.isNotBlank() } && !uiState.isLoading,
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
                            Text(
                                text       = "Create Account",
                                color      = SurfaceWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already have an account? ", color = Soft, fontSize = 13.sp)
                        Text(
                            text       = "Sign In",
                            color      = Accent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp,
                            modifier   = Modifier.clickable(onClick = onNavigateToLogin)
                        )
                    }
                }
            }
        }
    }
}
