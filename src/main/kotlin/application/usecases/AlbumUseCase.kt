package com.example.application.usecases
import com.example.application.dto.AlbumResponse
import com.example.application.dto.createAlbumRequest
import com.example.application.dto.updateAlbumRequest
import com.example.domain.interfaces.AlbumInterface
import com.example.domain.interfaces.ArtistInterface
import com.example.domain.models.Album
import com.example.infrastructure.repositories.AlbumRepositoryImpl
import com.example.infrastructure.repositories.ArtistRepositoryImpl
import java.util.UUID


class AlbumUseCase(
    private val albumRepository: AlbumInterface,
    private val artistRepository: ArtistInterface,
)

{
    suspend fun createAlbum(request: createAlbumRequest): AlbumResponse {
        val newAlbum = Album(
            id = UUID.randomUUID(),
            title = request.title,
            releaseYear = request.releaseYear,
            artistId = UUID.fromString(request.artistId),
            createdAt = null,
            updatedAt = null
        )

        val savedAlbum = albumRepository.createAlbum(newAlbum)
        return savedAlbum.toResponse()
    }

    suspend fun getAlbumById(id: String): AlbumResponse {
        val album = albumRepository.getAlbumById(id)
        return album.toResponse()
    }



    suspend fun getAllAlbums(): List<AlbumResponse> {
        return albumRepository.getAllAlbums().map { it.toResponse() }
    }

    suspend fun getAlbumsByArtist(artistId: String): List<AlbumResponse> {
        return albumRepository.getAlbumsByArtist(artistId).map { it.toResponse() }
    }

    suspend fun updateAlbum(id: String, request: updateAlbumRequest): AlbumResponse {
        val existing = albumRepository.getAlbumById(id)

        val modified = Album(
            id = existing.id,
            title = request.title ?: existing.title,
            releaseYear = request.releaseYear ?: existing.releaseYear,
            artistId = existing.artistId,
            createdAt = existing.createdAt,
            updatedAt = existing.updatedAt
        )

        val updated = albumRepository.updateAlbum(id, modified)
        return updated.toResponse()
    }

    suspend fun deleteAlbum(id: String): Boolean {
        return albumRepository.deleteAlbum(id)
    }

    private suspend fun Album.toResponse(): AlbumResponse {
        val artist = try {
            artistRepository.getArtistById(this.artistId.toString())
        } catch (e: Exception) {
            null
        }
        return AlbumResponse(
            id = this.id.toString(),
            title = this.title,
            releaseYear = this.releaseYear,
            artistId = this.artistId.toString(),
            artistName = artist?.name,
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString()
        )
    }
}
