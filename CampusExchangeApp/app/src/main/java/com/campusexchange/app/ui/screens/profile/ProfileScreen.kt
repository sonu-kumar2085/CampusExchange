package com.campusexchange.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campusexchange.app.ui.theme.*

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Re-fetch fresh data every time this tab becomes visible
    LaunchedEffect(Unit) { viewModel.load() }

    val uiState           = viewModel.uiState.collectAsState().value
    val snackbarHostState  = remember { SnackbarHostState() }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showUpdateAccountDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.changePasswordSuccess) {
        if (uiState.changePasswordSuccess) {
            snackbarHostState.showSnackbar("Password changed successfully")
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.changePasswordError) {
        uiState.changePasswordError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.updateAccountSuccess) {
        if (uiState.updateAccountSuccess) {
            snackbarHostState.showSnackbar("Account updated successfully")
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.updateAccountError) {
        uiState.updateAccountError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
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
            // ── Profile header band ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Primary, Accent)))
                    .statusBarsPadding()
                    .padding(bottom = 48.dp, top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite.copy(alpha = 0.18f))
                            .border(2.dp, SurfaceWhite.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = uiState.user?.fullName?.take(1)?.uppercase() ?: "?",
                            fontSize   = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = SurfaceWhite
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text       = uiState.user?.fullName ?: "—",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        color      = SurfaceWhite
                    )
                    Text(
                        text     = "@${uiState.user?.username ?: "—"}",
                        color    = Soft,
                        fontSize = 14.sp
                    )
                    if (!uiState.user?.email.isNullOrBlank()) {
                        Text(
                            text     = uiState.user!!.email,
                            color    = Soft.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ── Stats card (offset to overlap header) ────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-28).dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), clip = false)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceWhite)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileStatItem(
                    icon  = Icons.Default.MonetizationOn,
                    value = String.format("%.0f", uiState.wallet?.campusCoins ?: 0.0),
                    label = "Coins"
                )
                Divider(
                    modifier = Modifier
                        .height(48.dp)
                        .width(1.dp),
                    color    = Divider
                )
                ProfileStatItem(
                    icon  = Icons.Default.DirectionsWalk,
                    value = "${uiState.remoteSteps?.stepsCount ?: 0}",
                    label = "Total Steps"
                )
            }

            // bio card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-16).dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
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
                        Text(
                            text       = "About",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp,
                            color      = Soft
                        )
                        TextButton(onClick = { showEditDialog = true }) {
                            Icon(
                                imageVector        = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint               = Accent,
                                modifier           = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Edit", color = Accent, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = uiState.bio.ifBlank { "No bio yet. Tap Edit to add one." },
                        color      = if (uiState.bio.isBlank()) Soft else Primary,
                        fontSize   = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // ── Account section ──────────────────────────────────────────────
            Text(
                text       = "Account",
                color      = Soft,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier   = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            ProfileActionRow(
                icon    = Icons.Default.Person,
                label   = "Update Profile",
                onClick = { showUpdateAccountDialog = true }
            )

            ProfileActionRow(
                icon    = Icons.Default.Lock,
                label   = "Change Password",
                onClick = { showPasswordDialog = true }
            )

            Spacer(Modifier.height(24.dp))

            // Sign out
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = NegativeRed.copy(alpha = 0.08f)),
                border  = BorderStroke(1.dp, NegativeRed.copy(alpha = 0.4f)),
                shape   = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Logout,
                    contentDescription = null,
                    tint               = NegativeRed,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Sign Out",
                    color      = NegativeRed,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // ── Change Password Dialog ────────────────────────────────────────────────
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { old, new ->
                viewModel.changePassword(old, new)
                showPasswordDialog = false
            }
        )
    }

    // ── Edit Bio Dialog ───────────────────────────────────────────────────────
    if (showEditDialog) {
        EditBioDialog(
            currentBio = uiState.bio,
            onDismiss  = { showEditDialog = false },
            onConfirm  = { newBio ->
                viewModel.updateBio(newBio)
                showEditDialog = false
            }
        )
    }

    // ── Update Account Dialog ─────────────────────────────────────────────────
    if (showUpdateAccountDialog) {
        UpdateAccountDialog(
            currentFullName = uiState.user?.fullName ?: "",
            currentEmail    = uiState.user?.email ?: "",
            onDismiss       = { showUpdateAccountDialog = false },
            onConfirm       = { newName, newEmail ->
                viewModel.updateAccount(newName, newEmail)
                showUpdateAccountDialog = false
            }
        )
    }
}

// ── Components ────────────────────────────────────────────────────────────────

@Composable
private fun ProfileStatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Accent,
            modifier           = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(value, color = Primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Soft,    fontSize = 11.sp)
    }
}

@Composable
private fun ProfileActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp), clip = false)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(label, color = Primary, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Soft, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var oldPassword  by remember { mutableStateOf("") }
    var newPassword  by remember { mutableStateOf("") }
    var oldVisible   by remember { mutableStateOf(false) }
    var newVisible   by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(20.dp),
        title            = {
            Text("Change Password", color = Primary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value                = oldPassword,
                    onValueChange        = { oldPassword = it },
                    label                = { Text("Current Password") },
                    visualTransformation = if (oldVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon         = {
                        IconButton(onClick = { oldVisible = !oldVisible }) {
                            Icon(
                                if (oldVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                tint = Soft
                            )
                        }
                    },
                    singleLine = true,
                    modifier   = Modifier.fillMaxWidth(),
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = Accent,
                        unfocusedBorderColor    = Divider,
                        focusedLabelColor       = Accent,
                        unfocusedLabelColor     = Soft,
                        focusedTextColor        = Primary,
                        unfocusedTextColor      = Primary
                    )
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value                = newPassword,
                    onValueChange        = { newPassword = it },
                    label                = { Text("New Password") },
                    visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon         = {
                        IconButton(onClick = { newVisible = !newVisible }) {
                            Icon(
                                if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                tint = Soft
                            )
                        }
                    },
                    singleLine = true,
                    modifier   = Modifier.fillMaxWidth(),
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = Accent,
                        unfocusedBorderColor    = Divider,
                        focusedLabelColor       = Accent,
                        unfocusedLabelColor     = Soft,
                        focusedTextColor        = Primary,
                        unfocusedTextColor      = Primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    if (oldPassword.isNotBlank() && newPassword.isNotBlank())
                        onConfirm(oldPassword, newPassword)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Text("Update", color = SurfaceWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Soft)
            }
        }
    )
}

@Composable
fun EditBioDialog(currentBio: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var bio by remember { mutableStateOf(currentBio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(20.dp),
        title            = { Text("Edit Profile", color = Primary, fontWeight = FontWeight.Bold) },
        text  = {
            OutlinedTextField(
                value         = bio,
                onValueChange = { bio = it },
                label         = { Text("Bio") },
                maxLines      = 4,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Accent,
                    unfocusedBorderColor = Divider,
                    focusedLabelColor    = Accent,
                    unfocusedLabelColor  = Soft,
                    focusedTextColor     = Primary,
                    unfocusedTextColor   = Primary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(bio) },
                colors  = ButtonDefaults.buttonColors(containerColor = Primary),
                shape   = RoundedCornerShape(12.dp)
            ) {
                Text("Save", color = SurfaceWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Soft) }
        }
    )
}

@Composable
fun UpdateAccountDialog(
    currentFullName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var fullName by remember { mutableStateOf(currentFullName) }
    var email by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(20.dp),
        title            = { Text("Update Profile", color = Primary, fontWeight = FontWeight.Bold) },
        text  = {
            Column {
                OutlinedTextField(
                    value         = fullName,
                    onValueChange = { fullName = it },
                    label         = { Text("Full Name") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Divider,
                        focusedLabelColor    = Accent,
                        unfocusedLabelColor  = Soft,
                        focusedTextColor     = Primary,
                        unfocusedTextColor   = Primary
                    )
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = { Text("Email") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Divider,
                        focusedLabelColor    = Accent,
                        unfocusedLabelColor  = Soft,
                        focusedTextColor     = Primary,
                        unfocusedTextColor   = Primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        onConfirm(fullName, email)
                    }
                },
                colors  = ButtonDefaults.buttonColors(containerColor = Primary),
                shape   = RoundedCornerShape(12.dp)
            ) {
                Text("Update", color = SurfaceWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Soft) }
        }
    )
}
