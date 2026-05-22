package com.example.gabsstudentstay.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gabsstudentstay.frontend.AddListingScreen
import com.example.gabsstudentstay.frontend.BookingDetailScreen
import com.example.gabsstudentstay.frontend.BookingReceiptScreen
import com.example.gabsstudentstay.frontend.BookingScreen
import com.example.gabsstudentstay.frontend.ChatListScreen
import com.example.gabsstudentstay.frontend.ChatScreen
import com.example.gabsstudentstay.frontend.EditListingScreen
import com.example.gabsstudentstay.frontend.ImageViewerScreen
import com.example.gabsstudentstay.frontend.LessorBookingsScreen
import com.example.gabsstudentstay.frontend.ListingDetailScreen
import com.example.gabsstudentstay.frontend.LessorDashboard
import com.example.gabsstudentstay.frontend.LessorProfileScreen
import com.example.gabsstudentstay.frontend.MainScreen
import com.example.gabsstudentstay.frontend.NotificationsScreen
import com.example.gabsstudentstay.frontend.PreferencesScreen
import com.example.gabsstudentstay.frontend.SearchScreen
import com.example.gabsstudentstay.frontend.TenantBookingsScreen
import com.example.gabsstudentstay.frontend.TenantProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(Screen.LessorDashboard.route) {
            LessorDashboard(navController = navController)
        }
        composable(Screen.AddListing.route) {
            AddListingScreen(navController = navController)
        }

        composable(Screen.TenantBookings.route) {
            TenantBookingsScreen(navController = navController)
        }

        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }

        composable(Screen.LessorBookings.route) { backStackEntry ->
            val listingID = backStackEntry.arguments?.getString("listingID") ?: ""
            LessorBookingsScreen(
                listingID = listingID,
                navController = navController
            )
        }

        composable(Screen.Chat.route) { backStackEntry ->
            val chatID = backStackEntry.arguments?.getString("chatID") ?: ""
            val otherUserName = backStackEntry.arguments?.getString("otherUserName") ?: ""
            ChatScreen(
                chatID = chatID,
                otherUserName = otherUserName,
                navController = navController
            )
        }

        composable(Screen.BookingDetail.route) { backStackEntry ->
            val bookingID = backStackEntry.arguments?.getString("bookingID") ?: ""
            BookingDetailScreen(
                bookingID = bookingID,
                navController = navController
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(Screen.ChatList.route) {
            ChatListScreen(navController = navController)
        }

        composable(Screen.EditListing.route) { backStackEntry ->
            val listingID = backStackEntry.arguments?.getString("listingID") ?: ""
            EditListingScreen(
                listingID = listingID,
                navController = navController
            )
        }

        composable(Screen.Preferences.route) {
            PreferencesScreen(navController = navController)
        }

        composable(Screen.ImageViewer.route) {
            ImageViewerScreen(
                imageUrl = Screen.ImageViewer.currentImageUrl,
                navController = navController
            )
        }

        composable(Screen.BookingReceipt.route) { backStackEntry ->
            val bookingID = backStackEntry.arguments?.getString("bookingID") ?: ""
            val totalPrice = backStackEntry.arguments?.getString("totalPrice")?.toIntOrNull() ?: 0
            val roomID = backStackEntry.arguments?.getString("roomID") ?: ""
            BookingReceiptScreen(
                bookingID = bookingID,
                totalPrice = totalPrice,
                roomID = roomID,
                navController = navController
            )
        }

        composable(Screen.TenantProfile.route) {
            TenantProfileScreen(navController = navController)
        }


        composable(Screen.LessorProfile.route) {
            LessorProfileScreen(navController = navController)
        }

        composable(Screen.Booking.route) { backStackEntry ->
            val roomID = backStackEntry.arguments?.getString("roomID") ?: ""
            val listingID = backStackEntry.arguments?.getString("listingID") ?: ""
            BookingScreen(
                roomID = roomID,
                listingID = listingID,
                navController = navController
            )
        }

        composable(Screen.Preferences.route) {
            PreferencesScreen(
                navController = navController,
                onSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ListingDetail.route) { backStackEntry ->
            val listingID = backStackEntry.arguments?.getString("listingID") ?: ""
            ListingDetailScreen(
                listingID = listingID,
                navController = navController
            )
        }
    }
}