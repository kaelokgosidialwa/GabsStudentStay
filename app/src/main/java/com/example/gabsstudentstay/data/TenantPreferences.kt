package com.example.gabsstudentstay.data

import java.util.Date

data class TenantPreferences(
    val maxBudget: Int = 5000,
    val minCapacity: Int = 1,
    val preferredArea: String = "",
    val keywords: List<String> = emptyList(),
    val preferredTags: List<String> = emptyList(),
    val preferredMoveInDate: Date? = null  // ← NEW
)