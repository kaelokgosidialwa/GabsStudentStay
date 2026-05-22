package com.example.gabsstudentstay.frontend

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gabsstudentstay.data.Listing
import com.example.gabsstudentstay.data.ListingRepository
import com.example.gabsstudentstay.data.ListingTags
import com.example.gabsstudentstay.data.NotificationRepository
import com.example.gabsstudentstay.data.Room
import com.example.gabsstudentstay.data.StorageRepository
import com.example.gabsstudentstay.data.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class RoomFormState(
    val roomDesc: String = "",
    val capacity: String = "",
    val pricing: String = "",
    val imageUri: Uri? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(navController: NavController) {
    var shortDesc by remember { mutableStateOf("") }
    var longDesc by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    val rooms = remember { mutableStateListOf(RoomFormState()) }
    var listingImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }
    var availabilityDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LaunchedEffect(rooms.map { it.pricing }) {
        val prices = rooms.mapNotNull { it.pricing.toDoubleOrNull() }
        if (prices.isNotEmpty()) {
            val suggestedTag = ListingTags.suggestPriceTag(prices.min())
            selectedTags.removeAll(ListingTags.PRICE_RANGE)
            if (!selectedTags.contains(suggestedTag)) {
                selectedTags.add(suggestedTag)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Listing",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── STEP 1 - BASIC INFO ───
            item {
                SectionTitle("Step 1 — Basic Info")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = shortDesc,
                    onValueChange = { shortDesc = it },
                    label = { Text("Short Description (max 15 words)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = longDesc,
                    onValueChange = { longDesc = it },
                    label = { Text("Long Description (max 100 words)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 6
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (availabilityDate != null)
                            "Available from: ${dateFormat.format(availabilityDate!!)}"
                        else "Set Availability Date"
                    )
                }

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    availabilityDate = Date(it)
                                }
                                showDatePicker = false
                            }) { Text("Confirm") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }

            // ─── TAGS ───
            item {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle("Tags")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select tags that describe your listing. Price range is suggested automatically.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TagSelector(
                    selectedTags = selectedTags.toList(),
                    onTagToggled = { tag ->
                        if (selectedTags.contains(tag)) {
                            selectedTags.remove(tag)
                        } else {
                            selectedTags.add(tag)
                        }
                    }
                )
            }

            // ─── STEP 2 - LOCATION ───
            item {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle("Step 2 — Location")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter coordinates manually for now. Google Maps support coming soon.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }

            // ─── STEP 3 - ROOMS ───
            item {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle("Step 3 — Rooms")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add at least one room. Each room requires at least one image.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(rooms.size) { index ->
                RoomFormCard(
                    roomNumber = index + 1,
                    state = rooms[index],
                    onStateChange = { rooms[index] = it },
                    onDelete = if (rooms.size > 1) {
                        { rooms.removeAt(index) }
                    } else null
                )
            }

            item {
                OutlinedButton(
                    onClick = { rooms.add(RoomFormState()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Room",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Add Another Room")
                }
            }

            // ─── STEP 4 - LISTING IMAGE ───
            item {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle("Step 4 — Listing Image")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add at least one image of the overall listing.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                ImagePicker(
                    label = "Listing Image",
                    imageUri = listingImageUri,
                    onImageSelected = { listingImageUri = it }
                )
            }

            // ─── STEP 5 - REVIEW & SUBMIT ───
            item {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle("Step 5 — Review & Submit")
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        when {
                            shortDesc.isBlank() -> errorMessage = "Please enter a short description"
                            longDesc.isBlank() -> errorMessage = "Please enter a long description"
                            address.isBlank() -> errorMessage = "Please enter an address"
                            city.isBlank() -> errorMessage = "Please enter a city"
                            rooms.any { it.pricing.isBlank() } -> errorMessage = "Each room must have a price"
                            rooms.any { it.capacity.isBlank() } -> errorMessage = "Each room must have a capacity"
                            lat == null || lat < -90 || lat > 90 -> errorMessage = "Please enter a valid latitude (-90 to 90)"
                            lon == null || lon < -180 || lon > 180 -> errorMessage = "Please enter a valid longitude (-180 to 180)"
                            else -> {
                                isSubmitting = true
                                errorMessage = ""
                                submitListing(
                                    shortDesc = shortDesc,
                                    longDesc = longDesc,
                                    address = address,
                                    city = city,
                                    latitude = lat,
                                    longitude = lon,
                                    rooms = rooms,
                                    listingImageUri = listingImageUri,
                                    tags = selectedTags.toList(), // ← now passed correctly
                                    onSuccess = {
                                        isSubmitting = false
                                        navController.popBackStack()
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
                    Text(if (isSubmitting) "Submitting..." else "Submit Listing")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun RoomFormCard(
    roomNumber: Int,
    state: RoomFormState,
    onStateChange: (RoomFormState) -> Unit,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room $roomNumber",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Room",
                            tint = Color.Red
                        )
                    }
                }
            }

            ImagePicker(
                label = "Room Image (required)",
                imageUri = state.imageUri,
                onImageSelected = { onStateChange(state.copy(imageUri = it)) }
            )

            OutlinedTextField(
                value = state.roomDesc,
                onValueChange = { onStateChange(state.copy(roomDesc = it)) },
                label = { Text("Room Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.capacity,
                    onValueChange = { onStateChange(state.copy(capacity = it)) },
                    label = { Text("Capacity") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = state.pricing,
                    onValueChange = { onStateChange(state.copy(pricing = it)) },
                    label = { Text("Price (P)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        }
    }
}

fun submitListing(
    shortDesc: String,
    longDesc: String,
    address: String,
    city: String,
    latitude: Double,
    longitude: Double,
    rooms: List<RoomFormState>,
    listingImageUri: Uri?,
    tags: List<String>,  // ← receives tags from composable
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val uniquePart = UUID.randomUUID().toString().take(4).uppercase()
    val listingID = "L_$uniquePart"
    val firestore = FirebaseFirestore.getInstance()
    val roomLetters = ('A'..'Z').toList()

    UserRepository.getCurrentUser(
        onSuccess = { user ->
            if (user == null) {
                onError("No user logged in")
                return@getCurrentUser
            }

            val roomPrices = rooms.map { it.pricing.toDoubleOrNull() ?: 0.0 }
            val minPricing = roomPrices.minOrNull() ?: 0.0
            val maxPricing = roomPrices.maxOrNull() ?: 0.0
            val lat = latitude.coerceIn(-90.0, 90.0)
            val lon = longitude.coerceIn(-180.0, 180.0)

            fun saveListingAndRooms(listingImageUrl: String) {
                val listing = Listing(
                    listingID = listingID,
                    lessorID = user.userID,
                    shortDesc = shortDesc,
                    longDesc = longDesc,
                    sharedImages = if (listingImageUrl.isNotEmpty()) listOf(listingImageUrl) else emptyList(),
                    sharedDate = Date(),
                    totalRooms = rooms.size,
                    address = address,
                    city = city,
                    position = GeoPoint(lat, lon),
                    available = true,
                    minPricing = minPricing,
                    maxPricing = maxPricing,
                    tags = tags  // ← now uses the parameter correctly
                )

                ListingRepository.addListing(
                    listing = listing,
                    onSuccess = {
                        NotificationRepository.notifyMatchingTenants(listing = listing)

                        var roomsCompleted = 0

                        fun saveRoom(index: Int, roomImageUrl: String) {
                            val roomID = "${listingID}_${roomLetters[index]}"
                            val roomState = rooms[index]
                            val room = Room(
                                roomID = roomID,
                                listingID = listingID,
                                roomNumber = index + 1,
                                pricing = roomState.pricing.toDoubleOrNull()?.toInt() ?: 0,
                                capacity = roomState.capacity.toIntOrNull() ?: 0,
                                available = true,
                                roomImages = if (roomImageUrl.isNotEmpty()) listOf(roomImageUrl) else emptyList(),
                                roomDesc = roomState.roomDesc
                            )

                            firestore.collection("rooms")
                                .document(roomID)
                                .set(room)
                                .addOnSuccessListener {
                                    roomsCompleted++
                                    if (roomsCompleted == rooms.size) {
                                        onSuccess()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    onError(e.message ?: "Failed to save room")
                                }
                        }

                        rooms.forEachIndexed { index, roomState ->
                            val roomID = "${listingID}_${roomLetters[index]}"
                            if (roomState.imageUri != null) {
                                StorageRepository.uploadRoomImage(
                                    listingID = listingID,
                                    roomID = roomID,
                                    imageUri = roomState.imageUri,
                                    onSuccess = { roomImageUrl ->
                                        saveRoom(index, roomImageUrl)
                                    },
                                    onError = {
                                        saveRoom(index, "")
                                    }
                                )
                            } else {
                                saveRoom(index, "")
                            }
                        }
                    },
                    onError = { onError(it) }
                )
            }

            if (listingImageUri != null) {
                StorageRepository.uploadListingImage(
                    listingID = listingID,
                    imageUri = listingImageUri,
                    onSuccess = { listingImageUrl ->
                        saveListingAndRooms(listingImageUrl)
                    },
                    onError = {
                        saveListingAndRooms("")
                    }
                )
            } else {
                saveListingAndRooms("")
            }
        },
        onError = { onError("Failed to get current user") }
    )
}