package com.example.application.usecases

import com.example.application.dto.ArtistResponse
import com.example.application.dto.createArtistRequest
import com.example.application.dto.updateArtistRequest
import com.example.domain.interfaces.ArtistInterface
import com.example.domain.models.Artist
import java.time.OffsetDateTime
import java.util.UUID

class ArtistUseCases(private val artistRepository: ArtistInterface){

    suspend fun createArtist(request: createArtistRequest): ArtistResponse {
        val artist = Artist(
            id = UUID.randomUUID(),
            name = request.name,
            genre = request.genre,
            createdAt = null,
            updatedAt = null
        )

        val createdArtist = artistRepository.createArtist(artist)

        return ArtistResponse(
            id = createdArtist.id.toString(),
            name = createdArtist.name,
            genre = createdArtist.genre,
            createdAt =createdArtist.createdAt.toString(),
            updatedAt = createdArtist.updatedAt.toString()
        )
    }


    suspend fun getAllArtists(): List<ArtistResponse> {
        val artists = artistRepository.getAllArtists()
        return artists.map {it.toResponse()}
    }

    suspend fun getArtistById(id: String): ArtistResponse{
        val artist = artistRepository.getArtistById(id)
        return artist.toResponse()
    }

    suspend fun updateArtist(id: String, request: updateArtistRequest):ArtistResponse{
        val actualArtist = artistRepository.getArtistById(id)

        val updatedArtist = Artist(
            id = actualArtist.id,
            name = actualArtist.name,
            genre = actualArtist.genre,
            createdAt = actualArtist.createdAt,
            updatedAt = actualArtist.updatedAt
        )

        val artist = artistRepository.updateArtist(id, updatedArtist)
        return artist.toResponse()

    }

    suspend fun deleteArtist(id: String): Boolean{
        return artistRepository.deleteArtist(id)
    }

    private fun Artist.toResponse() = ArtistResponse (
        id = this.id.toString(),
        name = this.name,
        genre = this.genre,
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString()
    )
}
