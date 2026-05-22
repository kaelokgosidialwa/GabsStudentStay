package com.example.gabsstudentstay.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.example.gabsstudentstay.data.UserRepository
import com.example.gabsstudentstay.navigation.Screen
import com.example.gabsstudentstay.viewmodel.ListingViewModel

@Composable
fun MainScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        UserRepository.getCurrentUser(
            onSuccess = { user ->
                if (user?.role == "LESSOR") {
                    navController.navigate(Screen.LessorDashboard.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            },
            onError = { }
        )
    }

    Scaffold(
        topBar = { TopRow(navController = navController) },
        bottomBar = { BottomNavRow(navController = navController) }
    ) { innerPadding ->
        MainContent(
            innerPadding = innerPadding,
            navController = navController
        )
    }
}

@Composable
fun SignInPromptDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Sign In Required",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text("Sign in to start browsing, booking and inquiring about listings!")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    innerPadding: PaddingValues,
    navController: NavController,
    listingViewModel: ListingViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        listingViewModel.fetchListings()
    }

    val listings by listingViewModel.listings.collectAsState()
    val isLoading by listingViewModel.isLoading.collectAsState()
    val errorMessage by listingViewModel.errorMessage.collectAsState()
    val seedMessage by listingViewModel.seedMessage.collectAsState()
    val latestListings by listingViewModel.latestListings.collectAsState()
    val nearUBListings by listingViewModel.nearUBListings.collectAsState()
    val nearBACListings by listingViewModel.nearBACListings.collectAsState()
    val budgetFriendlyListings by listingViewModel.budgetFriendlyListings.collectAsState()
    val spaciousListings by listingViewModel.spaciousListings.collectAsState()
    val preferenceListings by listingViewModel.preferenceListings.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    // refresh listings when screen is resumed
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            listingViewModel.fetchListings()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
    ) {
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { listingViewModel.fetchListings() },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                errorMessage.isNotEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { listingViewModel.fetchListings() }) {
                            Text("Retry")
                        }
                    }
                }

                listings.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No listings found",
                            color = MaterialTheme.colorScheme.onBackground
                        )

                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // According to Preferences FIRST
                        if (preferenceListings.isNotEmpty()) {
                            stickyHeader { SectionHeader("According to Your Preferences") }
                            item {
                                CarouselListingCard(
                                    listings = preferenceListings,
                                    onListingClick = { listingID ->
                                        navController.navigate(Screen.listingDetail(listingID))
                                    }
                                )
                            }
                        }

                        // Latest Listings
                        if (latestListings.isNotEmpty()) {
                            stickyHeader { SectionHeader("Latest Listings") }
                            item {
                                BasicListingCard(
                                    listing = latestListings.first(),
                                    onClick = {
                                        navController.navigate(
                                            Screen.listingDetail(latestListings.first().listingID)
                                        )
                                    }
                                )
                            }
                        }

                        // Near UB
                        if (nearUBListings.isNotEmpty()) {
                            stickyHeader { SectionHeader("Near UB") }
                            item {
                                CarouselListingCard(
                                    listings = nearUBListings,
                                    onListingClick = { listingID ->
                                        navController.navigate(Screen.listingDetail(listingID))
                                    }
                                )
                            }
                        }

                        // Near BAC
                        if (nearBACListings.isNotEmpty()) {
                            stickyHeader { SectionHeader("Near BAC") }
                            item {
                                CarouselListingCard(
                                    listings = nearBACListings,
                                    onListingClick = { listingID ->
                                        navController.navigate(Screen.listingDetail(listingID))
                                    }
                                )
                            }
                        }

                        // Budget Friendly
                        if (budgetFriendlyListings.isNotEmpty()) {
                            stickyHeader { SectionHeader("Budget Friendly") }
                            item {
                                CarouselListingCard(
                                    listings = budgetFriendlyListings,
                                    onListingClick = { listingID ->
                                        navController.navigate(Screen.listingDetail(listingID))
                                    }
                                )
                            }
                        }

                        // Spacious Options
                        if (spaciousListings.isNotEmpty()) {
                            stickyHeader { SectionHeader("Spacious Options") }
                            item {
                                CarouselListingCard(
                                    listings = spaciousListings,
                                    onListingClick = { listingID ->
                                        navController.navigate(Screen.listingDetail(listingID))
                                    }
                                )
                            }
                        }

                        // fallback
                        if (latestListings.isEmpty() &&
                            nearUBListings.isEmpty() &&
                            nearBACListings.isEmpty() &&
                            budgetFriendlyListings.isEmpty() &&
                            spaciousListings.isEmpty() &&
                            preferenceListings.isEmpty()
                        ) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No listings match your filters",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}