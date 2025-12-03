package com.example.application.dto

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor



object StringOrNumberSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StringOrNumber", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String {
        return try {
            decoder.decodeString()
        } catch (e: Exception) {
            decoder.decodeDouble().toString()
        } //en caso de que no se  pueda con string lo da como numero
    }
}

@Serializable
data class createAlbumRequest(
    val title: String,
    val releaseYear: Int,
    @Serializable(with = StringOrNumberSerializer::class)
    val artistId: String
)

@Serializable
data class updateAlbumRequest(
    val title: String?,
    val releaseYear: Int?
)

@Serializable
data class AlbumResponse(
    val id: String,
    val title: String,
    val releaseYear: Int,
    val artistId: String,
    val artistName: String?,
    val createdAt: String,
    val updatedAt: String
)