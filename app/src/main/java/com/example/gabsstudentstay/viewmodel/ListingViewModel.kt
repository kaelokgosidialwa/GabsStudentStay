package com.example.gabsstudentstay.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gabsstudentstay.data.Listing
import com.example.gabsstudentstay.data.ListingFilter
import com.example.gabsstudentstay.data.ListingRepository
import com.example.gabsstudentstay.data.Room
import com.example.gabsstudentstay.data.TenantPreferences
import com.example.gabsstudentstay.data.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListingViewModel : ViewModel() {

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms

    private val _preferences = MutableStateFlow(TenantPreferences())
    val preferences: StateFlow<TenantPreferences> = _preferences

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _seedMessage = MutableStateFlow("")
    val seedMessage: StateFlow<String> = _seedMessage

    // filtered section states
    private val _latestListings = MutableStateFlow<List<Listing>>(emptyList())
    val latestListings: StateFlow<List<Listing>> = _latestListings

    private val _nearUBListings = MutableStateFlow<List<Listing>>(emptyList())
    val nearUBListings: StateFlow<List<Listing>> = _nearUBListings

    private val _nearBACListings = MutableStateFlow<List<Listing>>(emptyList())
    val nearBACListings: StateFlow<List<Listing>> = _nearBACListings

    private val _budgetFriendlyListings = MutableStateFlow<List<Listing>>(emptyList())
    val budgetFriendlyListings: StateFlow<List<Listing>> = _budgetFriendlyListings

    private val _spaciousListings = MutableStateFlow<List<Listing>>(emptyList())
    val spaciousListings: StateFlow<List<Listing>> = _spaciousListings

    private val _preferenceListings = MutableStateFlow<List<Listing>>(emptyList())
    val preferenceListings: StateFlow<List<Listing>> = _preferenceListings

    fun fetchListings() {
        _isLoading.value = true
        ListingRepository.getListings(
            onSuccess = { listings ->
                val available = ListingFilter.availableOnly(listings)
                _listings.value = available
                fetchRoomsAndPreferences(available)
            },
            onError = { error ->
                _errorMessage.value = error
                _isLoading.value = false
            }
        )
    }

    private fun fetchRoomsAndPreferences(listings: List<Listing>) {
        // fetch all rooms
        FirebaseFirestore.getInstance()
            .collection("rooms")
            .get()
            .addOnSuccessListener { result ->
                val rooms = result.documents.mapNotNull {
                    it.toObject(Room::class.java)
                }
                _rooms.value = rooms

                // fetch preferences then apply filters
                UserRepository.getCurrentUser(
                    onSuccess = { user ->
                        val prefs = user?.preferences ?: TenantPreferences()
                        _preferences.value = prefs
                        applyFilters(listings, rooms, prefs)
                        _isLoading.value = false
                    },
                    onError = {
                        applyFilters(listings, rooms, TenantPreferences())
                        _isLoading.value = false
                    }
                )
            }
            .addOnFailureListener {
                applyFilters(listings, _rooms.value, _preferences.value)
                _isLoading.value = false
            }
    }

    private fun applyFilters(
        listings: List<Listing>,
        rooms: List<Room>,
        preferences: TenantPreferences
    ) {
        // remove fully booked listings first
        val available = ListingFilter.removeFullyBooked(listings, rooms)

        _latestListings.value = ListingFilter.latestListings(available)
        _nearUBListings.value = ListingFilter.nearUB(available)
        _nearBACListings.value = ListingFilter.nearBAC(available)
        _budgetFriendlyListings.value = ListingFilter.budgetFriendly(
            available,
            maxBudget = preferences.maxBudget
        )
        _spaciousListings.value = ListingFilter.spaciousOptions(available, rooms)
        _preferenceListings.value = ListingFilter.accordingToPreferences(
            available,
            rooms,
            preferences
        )
    }

    fun fetchListingsByLessor(lessorID: String) {
        _isLoading.value = true
        Log.d("LISTING_DEBUG", "Fetching listings for lessor: $lessorID")
        ListingRepository.getListingsByLessor(
            lessorID = lessorID,
            onSuccess = { listings ->
                Log.d("LISTING_DEBUG", "Found ${listings.size} listings")
                listings.forEach { Log.d("LISTING_DEBUG", "Listing: ${it.listingID} lessorID: ${it.lessorID}") }
                _listings.value = listings
                _isLoading.value = false
            },
            onError = { error ->
                Log.d("LISTING_DEBUG", "Error: $error")
                _errorMessage.value = error
                _isLoading.value = false
            }
        )
    }

}