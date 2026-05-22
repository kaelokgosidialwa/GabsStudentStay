package com.example.gabsstudentstay.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gabsstudentstay.data.Booking
import com.example.gabsstudentstay.data.BookingStatus
import com.example.gabsstudentstay.data.ChatRepository
import com.example.gabsstudentstay.data.NotificationRepository
import com.example.gabsstudentstay.data.NotificationType
import com.example.gabsstudentstay.data.Room
import com.example.gabsstudentstay.data.User
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingID: String,
    navController: NavController
) {
    var booking by remember { mutableStateOf<Booking?>(null) }
    var tenant by remember { mutableStateOf<User?>(null) }
    var room by remember { mutableStateOf<Room?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(bookingID) {
        firestore.collection("bookings")
            .document(bookingID)
            .get()
            .addOnSuccessListener { document ->
                val fetchedBooking = document.toObject(Booking::class.java)
                booking = fetchedBooking
                if (fetchedBooking != null) {
                    UserRepository.getUserByID(
                        userID = fetchedBooking.tenantID,
                        onSuccess = { fetchedTenant -> tenant = fetchedTenant },
                        onError = { }
                    )
                    firestore.collection("rooms")
                        .document(fetchedBooking.roomID)
                        .get()
                        .addOnSuccessListener { roomDoc ->
                            room = roomDoc.toObject(Room::class.java)
                            isLoading = false
                        }
                        .addOnFailureListener { isLoading = false }
                }
            }
            .addOnFailureListener {
                errorMessage = "Failed to load booking"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking Details",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            booking != null -> {
                val currentBooking = booking!!
                val statusColor = when (currentBooking.status) {
                    BookingStatus.PENDING.name -> Color(0xFFFFC107)
                    BookingStatus.CONFIRMED.name -> Color.Green
                    BookingStatus.CANCELLED.name -> Color.Red
                    else -> Color.Gray
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // status header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Booking #${currentBooking.bookingID}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = statusColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = currentBooking.status,
                                    fontSize = 12.sp,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // tenant info + chat button
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Tenant")
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = tenant?.name ?: "Unknown",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = tenant?.email ?: "No email",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = tenant?.phone?.ifEmpty { "No phone" } ?: "No phone",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // chat with tenant button
                                Button(
                                    onClick = {
                                        UserRepository.getCurrentUser(
                                            onSuccess = { user ->
                                                if (user != null) {
                                                    ChatRepository.getOrCreateChat(
                                                        listingID = currentBooking.listingID,
                                                        tenantID = currentBooking.tenantID,
                                                        lessorID = user.userID,
                                                        onSuccess = { chatID ->
                                                            navController.navigate(
                                                                Screen.chat(
                                                                    chatID = chatID,
                                                                    otherUserName = tenant?.name ?: "Tenant"
                                                                )
                                                            )
                                                        },
                                                        onError = { }
                                                    )
                                                }
                                            },
                                            onError = { }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Chat with Tenant")
                                }
                            }
                        }
                    }

                    // room info
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Room")
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Room ${room?.roomNumber ?: ""}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = room?.roomDesc ?: "",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Capacity: ${room?.capacity ?: ""}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "P${room?.pricing ?: 0}/month",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // booking info
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Booking Info")
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Move In",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = dateFormat.format(currentBooking.moveInDate),
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Move Out",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = dateFormat.format(currentBooking.moveOutDate),
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Divider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total Price",
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "P${currentBooking.totalPrice}",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // accept/deny buttons
                    item {
                        if (currentBooking.status == BookingStatus.PENDING.name) {
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isUpdating = true
                                        firestore.collection("bookings")
                                            .document(bookingID)
                                            .update("status", BookingStatus.CANCELLED.name)
                                            .addOnSuccessListener {
                                                firestore.collection("rooms")
                                                    .document(currentBooking.roomID)
                                                    .update("available", true)
                                                    .addOnSuccessListener {
                                                        NotificationRepository.sendNotification(
                                                            userID = currentBooking.tenantID,
                                                            title = "Booking Denied",
                                                            message = "Your booking request has been denied by the lessor.",
                                                            type = NotificationType.BOOKING_DENIED,
                                                            relatedID = bookingID
                                                        )
                                                        isUpdating = false
                                                        navController.popBackStack()
                                                    }
                                            }
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isUpdating,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    )
                                ) {
                                    Text("Deny")
                                }

                                Button(
                                    onClick = {
                                        isUpdating = true
                                        firestore.collection("bookings")
                                            .document(bookingID)
                                            .update("status", BookingStatus.CONFIRMED.name)
                                            .addOnSuccessListener {
                                                NotificationRepository.sendNotification(
                                                    userID = currentBooking.tenantID,
                                                    title = "Booking Confirmed!",
                                                    message = "Your booking request has been accepted by the lessor. Welcome!",
                                                    type = NotificationType.BOOKING_ACCEPTED,
                                                    relatedID = bookingID
                                                )
                                                isUpdating = false
                                                navController.popBackStack()
                                            }
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isUpdating,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green
                                    )
                                ) {
                                    Text("Accept")
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}