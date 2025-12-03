package com.example.domain.models

import java.time.OffsetDateTime
import java.util.UUID

data class Track(
    val id: UUID,
    val title: String,
    val duration: Int,
    val albumId: UUID,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)
