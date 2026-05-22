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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gabsstudentstay.data.Booking
import com.example.gabsstudentstay.data.BookingStatus
import com.example.gabsstudentstay.data.ListingRepository
import com.example.gabsstudentstay.data.NotificationRepository
import com.example.gabsstudentstay.data.NotificationType
import com.example.gabsstudentstay.data.Room
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    roomID: String,
    listingID: String,
    navController: NavController
) {
    var room by remember { mutableStateOf<Room?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    // date states
    var moveInDate by remember { mutableStateOf<Date?>(null) }
    var moveOutDate by remember { mutableStateOf<Date?>(null) }
    var showMoveInPicker by remember { mutableStateOf(false) }
    var showMoveOutPicker by remember { mutableStateOf(false) }

    // payment states
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var navigateToReceipt by remember { mutableStateOf(false) }
    var completedBookingID by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // fetch room on launch
    LaunchedEffect(roomID) {
        FirebaseFirestore.getInstance()
            .collection("rooms")
            .document(roomID)
            .get()
            .addOnSuccessListener { document ->
                room = document.toObject(Room::class.java)
                isLoading = false
            }
            .addOnFailureListener {
                errorMessage = "Failed to load room"
                isLoading = false
            }
    }

    // calculate total price based on months between dates
    val totalPrice = remember(moveInDate, moveOutDate, room) {
        if (moveInDate != null && moveOutDate != null && room != null) {
            val diffMs = moveOutDate!!.time - moveInDate!!.time
            val months = (diffMs / (1000L * 60 * 60 * 24 * 30)).toInt().coerceAtLeast(1)
            months * room!!.pricing
        } else 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Book Room",
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

            room != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // room summary
                    item {
                        SectionTitle("Room Summary")
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
                                Text(
                                    text = "Room ${room!!.roomNumber}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = room!!.roomDesc,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Capacity: ${room!!.capacity}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "P${room!!.pricing}/month",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // date selection
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Select Dates")
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // move in date
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Move In",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { showMoveInPicker = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = moveInDate?.let { dateFormat.format(it) }
                                            ?: "Select",
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // move out date
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Move Out",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { showMoveOutPicker = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = moveOutDate?.let { dateFormat.format(it) }
                                            ?: "Select",
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        if (moveInDate != null && moveOutDate != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Per Month",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "P${room!!.pricing}",
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Price",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "P$totalPrice",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // payment form
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Payment Details")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mock payment — no real charges will be made",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // card number
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = {
                                if (it.length <= 16) cardNumber = it.filter { c -> c.isDigit() }
                            },
                            label = { Text("Card Number") },
                            placeholder = { Text("1234 5678 9012 3456") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = cardNumber.isNotEmpty() && cardNumber.length < 16,
                            supportingText = {
                                if (cardNumber.isNotEmpty() && cardNumber.length < 16) {
                                    Text("Card number must be 16 digits")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // expiry date
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }
                                    expiryDate = when {
                                        digits.length >= 4 -> "${digits.take(2)}/${digits.drop(2).take(2)}"
                                        digits.length >= 2 -> "${digits.take(2)}/${digits.drop(2)}"
                                        else -> digits
                                    }
                                },
                                label = { Text("Expiry") },
                                placeholder = { Text("MM/YY") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                isError = expiryDate.isNotEmpty() && expiryDate.length < 5,
                                supportingText = {
                                    if (expiryDate.isNotEmpty() && expiryDate.length < 5) {
                                        Text("Format: MM/YY")
                                    }
                                }
                            )

                            // cvv
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = {
                                    if (it.length <= 3) cvv = it.filter { c -> c.isDigit() }
                                },
                                label = { Text("CVV") },
                                placeholder = { Text("123") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                isError = cvv.isNotEmpty() && cvv.length < 3,
                                supportingText = {
                                    if (cvv.isNotEmpty() && cvv.length < 3) {
                                        Text("CVV must be 3 digits")
                                    }
                                }
                            )
                        }
                    }

                    // error message
                    item {
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // confirm button
                    item {
                        Button(
                            onClick = {
                                when {
                                    moveInDate == null -> errorMessage = "Please select a move in date"
                                    moveOutDate == null -> errorMessage = "Please select a move out date"
                                    moveOutDate!!.before(moveInDate) -> errorMessage = "Move out date must be after move in date"
                                    cardNumber.length < 16 -> errorMessage = "Please enter a valid card number"
                                    expiryDate.length < 5 -> errorMessage = "Please enter a valid expiry date"
                                    cvv.length < 3 -> errorMessage = "Please enter a valid CVV"
                                    else -> {
                                        isSubmitting = true
                                        errorMessage = ""
                                        submitBooking(
                                            roomID = roomID,
                                            listingID = listingID,
                                            moveInDate = moveInDate!!,
                                            moveOutDate = moveOutDate!!,
                                            totalPrice = totalPrice,
                                            onSuccess = { bookingID ->
                                                isSubmitting = false
                                                navController.navigate(
                                                    Screen.bookingReceipt(
                                                        bookingID = bookingID,
                                                        totalPrice = totalPrice,
                                                        roomID = roomID
                                                    )
                                                ) {
                                                    popUpTo(Screen.Booking.route) { inclusive = true }
                                                }
                                            },
                                            onError = {
                                                isSubmitting = false
                                                errorMessage = it
                                            }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(end = 8.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(if (isSubmitting) "Processing..." else "Confirm Booking — P$totalPrice")
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    // move in date picker
    if (showMoveInPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showMoveInPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        moveInDate = Date(it)
                    }
                    showMoveInPicker = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMoveInPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // move out date picker
    if (showMoveOutPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showMoveOutPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        moveOutDate = Date(it)
                    }
                    showMoveOutPicker = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMoveOutPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // booking confirmation dialog
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "Booking Confirmed!",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Your booking request has been submitted!")
                    Text(
                        text = "The lessor will review and accept or deny your request.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Total paid: P$totalPrice",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmation = false
                        navController.popBackStack()
                        navController.popBackStack()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}

fun submitBooking(
    roomID: String,
    listingID: String,
    moveInDate: Date,
    moveOutDate: Date,
    totalPrice: Int,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val bookingID = "B_${UUID.randomUUID().toString().take(4).uppercase()}"

    UserRepository.getCurrentUser(
        onSuccess = { user ->
            if (user == null) {
                onError("No user logged in")
                return@getCurrentUser
            }

            val booking = Booking(
                bookingID = bookingID,
                tenantID = user.userID,
                roomID = roomID,
                listingID = listingID,
                bookingDate = Date(),
                moveInDate = moveInDate,
                moveOutDate = moveOutDate,
                totalPrice = totalPrice,
                status = BookingStatus.PENDING.name,
                createdAt = Date()
            )

            firestore.collection("bookings")
                .document(bookingID)
                .set(booking)
                .addOnSuccessListener {
                    firestore.collection("rooms")
                        .document(roomID)
                        .update(
                            mapOf(
                                "available" to false,
                                "status" to "RESERVED"  // ← add status field to Room
                            )
                        )
                        .addOnSuccessListener { onSuccess(bookingID) }
                        .addOnFailureListener { e ->
                            onError(e.message ?: "Failed to update room")
                        }
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Failed to create booking")
                }
        },
        onError = { onError("Failed to get current user") }
    )
}