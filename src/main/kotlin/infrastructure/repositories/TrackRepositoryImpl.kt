package com.example.infrastructure.repositories

import com.example.domain.interfaces.TrackInterface
import com.example.domain.models.Track
import com.example.infrastructure.database.tables.AlbumesTable
import com.example.infrastructure.database.tables.TracksTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import plugins.DatabaseFactory.dbQuery
import java.time.OffsetDateTime
import java.util.UUID

class TrackRepositoryImpl : TrackInterface {

    private fun ResultRow.toTrack() = Track(
        id = this[TracksTable.id],
        title = this[TracksTable.title],
        duration = this[TracksTable.duration],
        albumId = this[TracksTable.albumId],
        createdAt = this[TracksTable.createdAt],
        updatedAt = this[TracksTable.updatedAt]
    )

    override suspend fun getTrackById(id: String): Track = dbQuery {
        try {
            TracksTable
                .selectAll()
                .where { TracksTable.id eq UUID.fromString(id) }
                .map { it.toTrack() }
                .singleOrNull()
                ?: throw NoSuchElementException("Track no encontrado: $id")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID inválido: $id")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllTracks(): List<Track> = dbQuery {
        TracksTable
            .selectAll()
            .orderBy(TracksTable.title to SortOrder.ASC)
            .map { it.toTrack() }
    }

    override suspend fun getTracksByAlbum(albumId: String): List<Track> = dbQuery {
        try {
            val uuid = UUID.fromString(albumId)

            // verificar que existe el álbum
            AlbumesTable
                .selectAll()
                .where { AlbumesTable.id eq uuid }
                .singleOrNull()
                ?: throw NoSuchElementException("Álbum no encontrado: $albumId")

            TracksTable
                .selectAll()
                .where { TracksTable.albumId eq uuid }
                .orderBy(TracksTable.id to SortOrder.ASC)
                .map { it.toTrack() }

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID de álbum inválido: $albumId")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createTrack(track: Track): Track = dbQuery {
        try {

            AlbumesTable
                .selectAll()
                .where { AlbumesTable.id eq track.albumId }
                .singleOrNull()
                ?: throw NoSuchElementException("Álbum no encontrado: ${track.albumId}")

            val timestamp = OffsetDateTime.now()

            TracksTable.insert {
                it[id] = track.id
                it[title] = track.title
                it[duration] = track.duration
                it[albumId] = track.albumId
                it[createdAt] = timestamp
                it[updatedAt] = timestamp
            }

            track.copy(
                createdAt = timestamp,
                updatedAt = timestamp
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateTrack(id: String, track: Track): Track = dbQuery {
        try {
            val uuid = UUID.fromString(id)

            TracksTable
                .selectAll()
                .where { TracksTable.id eq uuid }
                .singleOrNull()
                ?: throw NoSuchElementException("Track no encontrado: $id")

            val timestamp = OffsetDateTime.now()

            TracksTable.update({ TracksTable.id eq uuid }) {
                it[title] = track.title
                it[duration] = track.duration
                it[updatedAt] = timestamp
            }


            TracksTable
                .selectAll()
                .where { TracksTable.id eq uuid }
                .map { it.toTrack() }
                .single()

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID inválido: $id")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteTrack(id: String): Boolean = dbQuery {
        try {
            val uuid = UUID.fromString(id)

            TracksTable
                .selectAll()
                .where { TracksTable.id eq uuid }
                .singleOrNull()
                ?: throw NoSuchElementException("Track no encontrado: $id")

            val rowsDeleted = TracksTable.deleteWhere { TracksTable.id eq uuid }
            rowsDeleted > 0

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID inválido: $id")
        } catch (e: Exception) {
            throw e
        }
    }
}
