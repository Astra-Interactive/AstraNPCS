package com.astrainteractive.astranpcs.remote_api

data class Profile(
    val name: String,
    val id: String,
)

data class ProfileSkin(
    val id: String,
    val name: String,
    val properties: List<Property>,
) {
    data class Property(
        val name: String,
        val value: String,
        val signature: String,
    )
}