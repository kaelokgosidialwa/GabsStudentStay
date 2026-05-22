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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.gabsstudentstay.data.NotificationRepository
import com.example.gabsstudentstay.data.NotificationType
import com.example.gabsstudentstay.data.Room
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListingScreen(
    listingID: String,
    navController: NavController
) {
    var listing by remember { mutableStateOf<Listing?>(null) }
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // editable fields
    var shortDesc by remember { mutableStateOf("") }
    var longDesc by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var available by remember { mutableStateOf(true) }

    // room editing state
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    var showEditRoomSheet by remember { mutableStateOf(false) }
    var showDeleteRoomDialog by remember { mutableStateOf(false) }
    var roomToDelete by remember { mutableStateOf<Room?>(null) }

    val firestore = FirebaseFirestore.getInstance()
    // add state
    val selectedTags = remember { mutableStateListOf<String>() }

    LaunchedEffect(listing) {
        listing?.tags?.let { existingTags ->
            selectedTags.clear()
            selectedTags.addAll(existingTags)
        }
    }

    LaunchedEffect(listingID) {
        // fetch listing
        ListingRepository.getListingByID(
            listingID = listingID,
            onSuccess = { fetchedListing ->
                fetchedListing?.let {
                    listing = it
                    shortDesc = it.shortDesc
                    longDesc = it.longDesc
                    address = it.address
                    city = it.city
                    available = it.available
                }
                // fetch rooms
                firestore.collection("rooms")
                    .whereEqualTo("listingID", listingID)
                    .get()
                    .addOnSuccessListener { result ->
                        rooms = result.documents.mapNotNull { it.toObject(Room::class.java) }
                        isLoading = false
                    }
                    .addOnFailureListener { isLoading = false }
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Listing",
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

            listing != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // basic info section
                    item {
                        SectionTitle("Basic Info")
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
                            label = { Text("Long Description") },
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // availability toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Listing Available",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Switch(
                                checked = available,
                                onCheckedChange = { available = it }
                            )
                        }
                    }

                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Tags")
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

                    // save button
                    item {
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
                                isSaving = true
                                errorMessage = ""
                                firestore.collection("listings")
                                    .document(listingID)
                                    .update(
                                        mapOf(
                                            "shortDesc" to shortDesc,
                                            "longDesc" to longDesc,
                                            "address" to address,
                                            "city" to city,
                                            "available" to available,
                                            "tags" to selectedTags.toList()  // ← add this
                                        )
                                    )
                                    .addOnSuccessListener {
                                        isSaving = false
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        isSaving = false
                                        errorMessage = e.message ?: "Failed to save"
                                    }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(end = 8.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(if (isSaving) "Saving..." else "Save Changes")
                        }
                    }

                    // rooms section
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("Rooms")
                    }

                    items(rooms) { room ->
                        EditRoomCard(
                            room = room,
                            onEdit = {
                                selectedRoom = room
                                showEditRoomSheet = true
                            },
                            onDelete = {
                                roomToDelete = room
                                showDeleteRoomDialog = true
                            }
                        )
                    }

                    // delete listing button
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isDeleting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Delete Listing")
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    // edit room bottom sheet
    if (showEditRoomSheet && selectedRoom != null) {
        EditRoomBottomSheet(
            room = selectedRoom!!,
            onDismiss = { showEditRoomSheet = false },
            onSaved = { updatedRoom ->
                // update room in list
                rooms = rooms.map {
                    if (it.roomID == updatedRoom.roomID) updatedRoom else it
                }
                showEditRoomSheet = false
            }
        )
    }

    // delete room confirmation dialog
    if (showDeleteRoomDialog && roomToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteRoomDialog = false },
            title = {
                Text(
                    text = "Delete Room?",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text("Are you sure you want to delete Room ${roomToDelete!!.roomNumber}? This cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteRoomDialog = false
                        firestore.collection("rooms")
                            .document(roomToDelete!!.roomID)
                            .delete()
                            .addOnSuccessListener {
                                rooms = rooms.filter { it.roomID != roomToDelete!!.roomID }
                                roomToDelete = null
                            }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteRoomDialog = false
                    roomToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // delete listing confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Listing?",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text("This will permanently delete the listing and all its rooms. Any active bookings will be cancelled and tenants will be notified.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        isDeleting = true
                        deleteListing(
                            listingID = listingID,
                            rooms = rooms,
                            onSuccess = {
                                isDeleting = false
                                navController.popBackStack()
                                navController.popBackStack()
                            },
                            onError = {
                                isDeleting = false
                                errorMessage = it
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EditRoomCard(
    room: Room,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room ${room.roomNumber}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (room.available) "Available" else "Unavailable",
                    fontSize = 12.sp,
                    color = if (room.available) Color.Green else Color.Red,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = room.roomDesc,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Capacity: ${room.capacity}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "P${room.pricing}/month",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoomBottomSheet(
    room: Room,
    onDismiss: () -> Unit,
    onSaved: (Room) -> Unit
) {
    var roomDesc by remember { mutableStateOf(room.roomDesc) }
    var pricing by remember { mutableStateOf(room.pricing.toString()) }
    var capacity by remember { mutableStateOf(room.capacity.toString()) }
    var available by remember { mutableStateOf(room.available) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    val firestore = FirebaseFirestore.getInstance()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Edit Room ${room.roomNumber}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = roomDesc,
                onValueChange = { roomDesc = it },
                label = { Text("Room Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacity") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = pricing,
                    onValueChange = { pricing = it },
                    label = { Text("Price (P)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room Available",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(
                    checked = available,
                    onCheckedChange = { available = it }
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = {
                    when {
                        roomDesc.isBlank() -> errorMessage = "Please enter a description"
                        pricing.isBlank() -> errorMessage = "Please enter a price"
                        capacity.isBlank() -> errorMessage = "Please enter a capacity"
                        else -> {
                            isSaving = true
                            val updatedRoom = room.copy(
                                roomDesc = roomDesc,
                                pricing = pricing.toIntOrNull() ?: room.pricing,
                                capacity = capacity.toIntOrNull() ?: room.capacity,
                                available = available
                            )
                            firestore.collection("rooms")
                                .document(room.roomID)
                                .update(
                                    mapOf(
                                        "roomDesc" to roomDesc,
                                        "pricing" to (pricing.toIntOrNull() ?: room.pricing),
                                        "capacity" to (capacity.toIntOrNull() ?: room.capacity),
                                        "available" to available
                                    )
                                )
                                .addOnSuccessListener {
                                    isSaving = false
                                    onSaved(updatedRoom)
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false
                                    errorMessage = e.message ?: "Failed to save"
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(if (isSaving) "Saving..." else "Save Room")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun deleteListing(
    listingID: String,
    rooms: List<Room>,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    // step 1 - find all active bookings for this listing
    firestore.collection("bookings")
        .whereEqualTo("listingID", listingID)
        .whereEqualTo("status", "PENDING")
        .get()
        .addOnSuccessListener { bookingResult ->
            val batch = firestore.batch()

            // cancel all pending bookings and notify tenants
            bookingResult.documents.forEach { doc ->
                batch.update(doc.reference, "status", "CANCELLED")
                val tenantID = doc.getString("tenantID") ?: ""
                if (tenantID.isNotEmpty()) {
                    NotificationRepository.sendNotification(
                        userID = tenantID,
                        title = "Listing Removed",
                        message = "A listing you booked has been removed by the lessor. Your booking has been cancelled.",
                        type = NotificationType.LISTING_DELETED,
                        relatedID = listingID
                    )
                }
            }

            // step 2 - delete all rooms
            rooms.forEach { room ->
                val roomRef = firestore.collection("rooms").document(room.roomID)
                batch.delete(roomRef)
            }

            // step 3 - delete the listing
            val listingRef = firestore.collection("listings").document(listingID)
            batch.delete(listingRef)

            // commit all at once
            batch.commit()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Failed to delete listing")
                }
        }
        .addOnFailureListener { e ->
            onError(e.message ?: "Failed to fetch bookings")
        }
}