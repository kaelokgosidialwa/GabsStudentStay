package com.example.gabsstudentstay.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object LessorDashboard : Screen("lessor_dashboard")
    object ListingDetail : Screen("listing_detail/{listingID}")
    object AddListing : Screen("add_listing")
    object Preferences : Screen("preferences")

    object Search : Screen("search")
    object Booking : Screen("booking/{roomID}/{listingID}")
    object LessorProfile : Screen("lessor_profile")
    object ChatList : Screen("chat_list")
    object TenantBookings : Screen("tenant_bookings")
    object Notifications : Screen("notifications")
    object ImageViewer : Screen("image_viewer") {
        var currentImageUrl = ""
    }
    object Chat : Screen("chat/{chatID}/{otherUserName}")
    object LessorBookings : Screen("lessor_bookings/{listingID}")
    object BookingDetail : Screen("booking_detail/{bookingID}")
    object TenantProfile : Screen("tenant_profile")
    object EditListing : Screen("edit_listing/{listingID}")
    object BookingReceipt : Screen("booking_receipt/{bookingID}/{totalPrice}/{roomID}")

    companion object {
        fun listingDetail(listingID: String) = "listing_detail/$listingID"
        fun chat(chatID: String, otherUserName: String) = "chat/$chatID/$otherUserName"
        fun booking(roomID: String, listingID: String) = "booking/$roomID/$listingID"
        fun editListing(listingID: String) = "edit_listing/$listingID"
        fun lessorBookings(listingID: String) = "lessor_bookings/$listingID"
        fun bookingDetail(bookingID: String) = "booking_detail/$bookingID"
        fun bookingReceipt(bookingID: String, totalPrice: Int, roomID: String) =
            "booking_receipt/$bookingID/$totalPrice/$roomID"
        fun imageViewer(imageUrl: String): String {
            val encoded = android.net.Uri.encode(imageUrl)
            return "image_viewer/$encoded"
        }
    }
}