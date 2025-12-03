package com.example.application.dto

import kotlinx.serialization.Serializable

@Serializable
data class createArtistRequest(
    val name: String,
    val genre: String
)

@Serializable
data class updateArtistRequest(
    val name: String?,
    val genre: String?
)

@Serializable
data class ArtistResponse(
    val id: String,
    val name: String,
    val genre: String?,
    val createdAt: String,
    val updatedAt: String
)