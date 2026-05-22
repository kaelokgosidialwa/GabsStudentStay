package com.example.gabsstudentstay.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gabsstudentstay.data.ChatRepository
import com.example.gabsstudentstay.data.Listing
import com.example.gabsstudentstay.data.ListingRepository
import com.example.gabsstudentstay.data.Room
import com.example.gabsstudentstay.data.User
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingID: String,
    navController: NavController
) {
    var listing by remember { mutableStateOf<Listing?>(null) }
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var lessor by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isSignedIn by remember { mutableStateOf(UserRepository.isSignedIn()) }
    var showInquireDialog by remember { mutableStateOf(false) }

    LaunchedEffect(listingID) {
        ListingRepository.getListingByID(
            listingID = listingID,
            onSuccess = { fetchedListing ->
                listing = fetchedListing
                if (fetchedListing != null) {
                    UserRepository.getUserByID(
                        userID = fetchedListing.lessorID,
                        onSuccess = { fetchedLessor -> lessor = fetchedLessor },
                        onError = { }
                    )
                    FirebaseFirestore.getInstance()
                        .collection("rooms")
                        .whereEqualTo("listingID", listingID)
                        .get()
                        .addOnSuccessListener { result ->
                            rooms = result.documents.mapNotNull { it.toObject(Room::class.java) }
                            isLoading = false
                        }
                        .addOnFailureListener { isLoading = false }
                }
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
                title = { Text(listing?.shortDesc ?: "Listing Detail") },
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

            listing != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 120.dp  // ← increased for two buttons
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // image carousel
                        item {
                            if (listing!!.sharedImages.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(listing!!.sharedImages) { imageUrl ->
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = "Listing image",
                                            modifier = Modifier
                                                .width(280.dp)
                                                .height(180.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    Screen.ImageViewer.currentImageUrl = imageUrl
                                                    navController.navigate(Screen.ImageViewer.route)
                                                },
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // basic info
                        item {
                            Text(
                                text = listing!!.shortDesc,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "P${listing!!.minPricing.roundToInt()} - P${listing!!.maxPricing.roundToInt()}/month",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${listing!!.address}, ${listing!!.city}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // tags section
                        item {
                            if (!listing!!.tags.isNullOrEmpty()) {
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tags",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TagDisplay(tags = listing!!.tags)
                            }
                        }

                        // long description
                        item {
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "About this listing",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = listing!!.longDesc,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                        }

                        // rooms section
                        item {
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rooms (${listing!!.totalRooms})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        items(rooms) { room ->
                            RoomDetailCard(
                                room = room,
                                isSignedIn = isSignedIn,
                                navController = navController
                            )
                        }

                        // lessor info
                        item {
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Posted by",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
                                Text(
                                    text = lessor?.name ?: "Unknown",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    // bottom action area
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        if (isSignedIn) {
                            // signed in — show inquire + chat buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showInquireDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Inquire")
                                }

                                Button(
                                    onClick = {
                                        UserRepository.getCurrentUser(
                                            onSuccess = { user ->
                                                if (user != null && listing != null) {
                                                    ChatRepository.getOrCreateChat(
                                                        listingID = listing!!.listingID,
                                                        tenantID = user.userID,
                                                        lessorID = listing!!.lessorID,
                                                        onSuccess = { chatID ->
                                                            navController.navigate(
                                                                Screen.chat(
                                                                    chatID = chatID,
                                                                    otherUserName = lessor?.name ?: "Lessor"
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
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Chat")
                                }
                            }
                        } else {
                            // guest — show sign up prompt
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Gray.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sign up to start inquiring",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // inquire dialog
    if (showInquireDialog && lessor != null) {
        AlertDialog(
            onDismissRequest = { showInquireDialog = false },
            title = {
                Text(
                    text = "Contact ${lessor!!.name}",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Email: ",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = lessor!!.email.ifEmpty { "Not provided" },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Phone: ",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = lessor!!.phone.ifEmpty { "Not provided" },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInquireDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun RoomDetailCard(
    room: Room,
    isSignedIn: Boolean,
    navController: NavController
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
            // room image or placeholder
            if (room.roomImages.isNotEmpty()) {
                AsyncImage(
                    model = room.roomImages.first(),
                    contentDescription = "Room image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Room ${room.roomNumber}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

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

            Text(
                text = when {
                    room.available -> "Available"
                    room.status == "RESERVED" -> "Reserved"
                    else -> "Not Available"
                },
                fontSize = 12.sp,
                color = when {
                    room.available -> Color.Green
                    room.status == "RESERVED" -> Color(0xFFFFC107) // amber
                    else -> Color.Red
                },
                fontWeight = FontWeight.Medium
            )

            Button(
                onClick = {
                    navController.navigate(
                        Screen.booking(room.roomID, room.listingID)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isSignedIn && room.available
            ) {
                Text("Book This Room")
            }
        }
    }
}