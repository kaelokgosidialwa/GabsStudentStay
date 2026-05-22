package com.example.gabsstudentstay.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gabsstudentstay.data.NotificationRepository
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessorTopRow(navController: NavController) {
    var showAccountSheet by remember { mutableStateOf(false) }
    var isSignedIn by remember { mutableStateOf(UserRepository.isSignedIn()) }
    var unreadCount by remember { mutableStateOf(0) }
    val sheetState = rememberModalBottomSheetState()

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
        // Left side - account + notifications bell with badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Account circle
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { showAccountSheet = true },
                tint = MaterialTheme.colorScheme.onSurface
            )

            // Notifications bell with badge
            Box {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            navController.navigate(Screen.Notifications.route)
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

        // Middle - Add New Listing button
        Button(
            onClick = { navController.navigate(Screen.AddListing.route) },
            modifier = Modifier
                .weight(0.6f)
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Listing",
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "New Listing",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Right side - chat + profile
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Messages icon
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Messages",
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        navController.navigate(Screen.ChatList.route)
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
                },
                onSignOutSuccess = {
                    isSignedIn = false
                    showAccountSheet = false
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.LessorDashboard.route) { inclusive = true }
                    }
                },
                onDismiss = { showAccountSheet = false }
            )
        }
    }
}