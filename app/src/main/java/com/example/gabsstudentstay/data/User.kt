package com.example.gabsstudentstay.data

import java.util.Date

enum class UserRole {
    LESSOR,
    TENANT
}

data class User(
    val userID: String = "",
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = UserRole.TENANT.name,
    val profileImage: String = "",
    val createdAt: Date = Date(),
    val preferences: TenantPreferences = TenantPreferences()
)