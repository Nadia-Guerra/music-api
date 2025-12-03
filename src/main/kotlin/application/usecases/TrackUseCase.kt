package com.example.application.usecases


import com.example.domain.interfaces.ArtistInterface
import com.example.domain.interfaces.TrackInterface
import com.example.domain.models.Track
import java.util.UUID
import com.example.application.dto.createTrackRequest
import com.example.application.dto.TrackResponse
import com.example.application.dto.updateTrackRequest
import com.example.domain.interfaces.AlbumInterface


class TracksUseCase(
    private val trackRepository: TrackInterface,
    private val albumRepository: AlbumInterface,
    private val artistRepository: ArtistInterface
) {

    suspend fun createTrack(request: createTrackRequest): TrackResponse {
        val newTrack = Track(
            id = UUID.randomUUID(),
            title = request.title,
            duration = request.duration,
            albumId = UUID.fromString(request.albumId),
            createdAt = null,
            updatedAt = null
        )

        val saved = trackRepository.createTrack(newTrack)
        return saved.toResponse()
    }

    suspend fun getTrack(id: String): TrackResponse {
        val track = trackRepository.getTrackById(id)
        return track.toResponse()
    }

    suspend fun getAllTracks(): List<TrackResponse> {
        return trackRepository.getAllTracks().map { it.toResponse() }
    }

    suspend fun getTracksByAlbum(albumId: String): List<TrackResponse> {
        return trackRepository.getTracksByAlbum(albumId).map { it.toResponse() }
    }

    suspend fun updateTrack(id: String, request: updateTrackRequest): TrackResponse {
        val existing = trackRepository.getTrackById(id)

        val modified = Track(
            id = existing.id,
            title = request.title ?: existing.title,
            duration = request.duration ?: existing.duration,
            albumId = existing.albumId,
            createdAt = existing.createdAt,
            updatedAt = existing.updatedAt
        )

        val updated = trackRepository.updateTrack(id, modified)
        return updated.toResponse()
    }

    suspend fun deleteTrack(id: String): Boolean {
        return trackRepository.deleteTrack(id)
    }

    private suspend fun Track.toResponse(): TrackResponse {
        val album = try {
            albumRepository.getAlbumById(this.albumId.toString())
        } catch (e: Exception) {
            null
        }

        val artist = if (album != null) {
            try {
                artistRepository.getArtistById(album.artistId.toString())
            } catch (e: Exception) {
                null
            }
        } else null

        return TrackResponse(
            id = this.id.toString(),
            title = this.title,
            duration = this.duration,
            durationFormatted = formatDuration(this.duration),
            albumId = this.albumId.toString(),
            albumTitle = album?.title,
            artistName = artist?.name,
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString()
        )
    }

    private fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }
}
