package com.example.domain.models

import java.util.UUID
import java.time.OffsetDateTime



data class Album(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val artistId: UUID,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)