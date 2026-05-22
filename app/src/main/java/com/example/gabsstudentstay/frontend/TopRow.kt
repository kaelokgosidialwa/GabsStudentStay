package com.example.gabsstudentstay.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gabsstudentstay.data.NotificationRepository
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.data.UserRole
import com.example.gabsstudentstay.navigation.Screen

enum class AccountSheetView {
    HOME,
    SIGN_IN,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopRow(navController: NavController) {
    var showAccountSheet by remember { mutableStateOf(false) }
    var isSignedIn by remember { mutableStateOf(UserRepository.isSignedIn()) }
    var showPhonePrompt by remember { mutableStateOf(false) }
    var unreadCount by remember { mutableStateOf(0) }  // ← moved up here
    val sheetState = rememberModalBottomSheetState()

    // ← moved up here
    LaunchedEffect(Unit) {
        UserRepository.getCurrentUser(
            onSuccess = { user ->
                user?.let {
                    NotificationRepository.getUnreadCount(
                        userID = it.userID,
                        onSuccess = { count -> unreadCount = count },
                        onError = { }
                    )
                }
            },
            onError = { }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { showAccountSheet = true },
                tint = MaterialTheme.colorScheme.onSurface
            )

            Box {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            if (UserRepository.isSignedIn()) {
                                navController.navigate(Screen.Notifications.route)
                            }
                        },
                    tint = MaterialTheme.colorScheme.onSurface
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(0.6f)
                .height(36.dp)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { navController.navigate(Screen.Search.route) },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Search...",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .size(36.dp)
                    .clickable { },
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Options",
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        if (UserRepository.isSignedIn()) {
                            navController.navigate(Screen.Preferences.route)
                        }
                    },
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    if (showAccountSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSheet = false },
            sheetState = sheetState
        ) {
            AccountBottomSheet(
                isSignedIn = isSignedIn,
                navController = navController,  // ← add this
                onSignInSuccess = {
                    isSignedIn = true
                    showAccountSheet = false
                    UserRepository.getCurrentUser(
                        onSuccess = { user ->
                            if (user?.role == "LESSOR") {
                                if (user.phone.isEmpty()) {
                                    showPhonePrompt = true
                                } else {
                                    navController.navigate(Screen.LessorDashboard.route) {
                                        popUpTo(Screen.Main.route) { inclusive = true }
                                    }
                                }
                            }
                        },
                        onError = { }
                    )
                },
                onSignOutSuccess = {
                    isSignedIn = false
                    showAccountSheet = false
                },
                onDismiss = { showAccountSheet = false }
            )
        }
    }

    // phone prompt dialog
    if (showPhonePrompt) {
        AlertDialog(
            onDismissRequest = {
                showPhonePrompt = false
                navController.navigate(Screen.LessorDashboard.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            },
            title = {
                Text(
                    text = "Add Your Phone Number",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text("Tenants need your phone number to contact you about listings. Please add one to your profile.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPhonePrompt = false
                        navController.navigate(Screen.LessorProfile.route)
                    }
                ) {
                    Text("Add Now")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPhonePrompt = false
                        navController.navigate(Screen.LessorDashboard.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("Later")
                }
            }
        )
    }
}

@Composable
fun AccountBottomSheet(
    isSignedIn: Boolean,
    navController: NavController,
    onSignInSuccess: () -> Unit,
    onSignOutSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var currentView by remember { mutableStateOf(AccountSheetView.HOME) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Text(
            text = if (isSignedIn) "Welcome Back!" else "Guest User",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isSignedIn) {
            Button(
                onClick = {
                    onDismiss()
                    navController.navigate(Screen.TenantProfile.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Profile")
            }

            Button(
                onClick = {
                    UserRepository.signOut()
                    onSignOutSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Sign Out")
            }
        } else {
            when (currentView) {
                AccountSheetView.HOME -> {
                    Button(
                        onClick = { currentView = AccountSheetView.SIGN_IN },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign In")
                    }
                    OutlinedButton(
                        onClick = { currentView = AccountSheetView.REGISTER },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register")
                    }
                }
                AccountSheetView.SIGN_IN -> {
                    SignInForm(
                        onSuccess = { onSignInSuccess() },
                        onError = { errorMessage = it },
                        onBack = { currentView = AccountSheetView.HOME }
                    )
                }
                AccountSheetView.REGISTER -> {
                    RegisterForm(
                        onSuccess = { onSignInSuccess() },
                        onError = { errorMessage = it },
                        onBack = { currentView = AccountSheetView.HOME }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SignInForm(
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onBack: () -> Unit
) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = emailOrUsername,
        onValueChange = { emailOrUsername = it },
        label = { Text("Email or Username") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation()
    )

    Button(
        onClick = {
            isLoading = true
            UserRepository.signIn(
                emailOrUsername = emailOrUsername,
                password = password,
                onSuccess = {
                    isLoading = false
                    onSuccess()
                },
                onError = {
                    isLoading = false
                    onError(it)
                }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading && emailOrUsername.isNotEmpty() && password.isNotEmpty()
    ) {
        Text(if (isLoading) "Signing In..." else "Sign In")
    }

    OutlinedButton(
        onClick = { onBack() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Back")
    }
}

@Composable
fun RegisterForm(
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.TENANT) }
    var isLoading by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Full Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = username,
        onValueChange = { username = it.lowercase().replace(" ", "") },
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("e.g. john_doe") }
    )

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation()
    )

    Text(
        text = "I am a:",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { selectedRole = UserRole.TENANT },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedRole == UserRole.TENANT)
                    Color(0xFF6200EE) else Color.LightGray
            )
        ) {
            Text("Tenant")
        }
        Button(
            onClick = { selectedRole = UserRole.LESSOR },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedRole == UserRole.LESSOR)
                    Color(0xFF6200EE) else Color.LightGray
            )
        ) {
            Text("Lessor")
        }
    }

    Button(
        onClick = {
            isLoading = true
            UserRepository.signUp(
                email = email,
                password = password,
                name = name,
                username = username,
                role = selectedRole,
                onSuccess = {
                    isLoading = false
                    onSuccess()
                },
                onError = {
                    isLoading = false
                    onError(it)
                }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading && name.isNotEmpty() &&
                username.isNotEmpty() &&
                email.isNotEmpty() && password.isNotEmpty()
    ) {
        Text(if (isLoading) "Registering..." else "Register")
    }

    OutlinedButton(
        onClick = { onBack() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Back")
    }
}