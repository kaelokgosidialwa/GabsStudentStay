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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gabsstudentstay.data.Listing
import androidx.compose.runtime.LaunchedEffect
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.viewmodel.ListingViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.gabsstudentstay.data.BookingStatus
import com.example.gabsstudentstay.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

@Composable
fun LessorDashboard(navController: NavController) {
    Scaffold(
        topBar = { LessorTopRow(navController = navController) }
        // no bottom bar for lessor
    ) { innerPadding ->
        LessorContent(
            innerPadding = innerPadding,
            navController = navController
        )
    }
}

@Composable
fun LessorContent(
    innerPadding: PaddingValues,
    navController: NavController,
    listingViewModel: ListingViewModel = viewModel()
) {
    val listings by listingViewModel.listings.collectAsState()
    val isLoading by listingViewModel.isLoading.collectAsState()
    val errorMessage by listingViewModel.errorMessage.collectAsState()
    val bookingCounts = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    // ← THIS WAS MISSING - fetches lessor's listings on launch
    LaunchedEffect(Unit) {
        UserRepository.getCurrentUser(
            onSuccess = { user ->
                user?.let {
                    listingViewModel.fetchListingsByLessor(it.userID)
                }
            },
            onError = { }
        )
    }

    // fetch booking counts when listings load
    LaunchedEffect(listings) {
        if (listings.isNotEmpty()) {
            val counts = mutableMapOf<String, Int>()
            var completed = 0
            listings.forEach { listing ->
                FirebaseFirestore.getInstance()
                    .collection("bookings")
                    .whereEqualTo("listingID", listing.listingID)
                    .whereEqualTo("status", BookingStatus.PENDING.name)
                    .get()
                    .addOnSuccessListener { result ->
                        counts[listing.listingID] = result.size()
                        completed++
                        if (completed == listings.size) {
                            bookingCounts.value = counts.toMap()
                        }
                    }
            }
        }
    }

    // rest of LessorContent unchanged
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            errorMessage.isNotEmpty() -> {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            listings.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No listings yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap New Listing to add your first one",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = "My Listings",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    items(listings) { listing ->
                        LessorListingCard(
                            listing = listing,
                            bookingCount = bookingCounts.value[listing.listingID] ?: 0,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessorListingCard(
    listing: Listing,
    bookingCount: Int,
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
            modifier = Modifier.padding(12.dp)
        ) {
            if (listing.sharedImages.isNotEmpty()) {
                AsyncImage(
                    model = listing.sharedImages.first(),
                    contentDescription = listing.shortDesc,
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
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = listing.shortDesc,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "P${listing.minPricing.roundToInt()} - P${listing.maxPricing.roundToInt()}/month",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${listing.totalRooms} rooms",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (listing.available) Color.Green else Color.Red
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (listing.available) "Available" else "Unavailable",
                        fontSize = 12.sp,
                        color = if (listing.available) Color.Green else Color.Red
                    )
                }
                Text(
                    text = listing.city,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // edit button
                OutlinedButton(
                    onClick = {
                        navController.navigate(
                            Screen.editListing(listing.listingID)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }

                // bookings button with badge - only shows if bookings > 0
                if (bookingCount > 0) {
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = {
                                navController.navigate(
                                    Screen.lessorBookings(listing.listingID)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Bookings")
                        }
                        // badge
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC107)) // amber
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (bookingCount > 9) "9+" else bookingCount.toString(),
                                fontSize = 10.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}