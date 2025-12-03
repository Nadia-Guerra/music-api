package com.example.infrastructure.repositories

import com.example.domain.interfaces.AlbumInterface
import com.example.domain.models.Album
import com.example.infrastructure.database.tables.AlbumesTable
import com.example.infrastructure.database.tables.ArtistsTable
import java.time.OffsetDateTime
import java.util.UUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import plugins.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq





class AlbumRepositoryImpl : AlbumInterface {

    private fun ResultRow.toAlbum() = Album(
        id = this[AlbumesTable.id],
        title = this[AlbumesTable.title],
        releaseYear = this[AlbumesTable.releaseYear],
        artistId = this[AlbumesTable.artistId],
        createdAt = this[AlbumesTable.createdAt],
        updatedAt = this[AlbumesTable.updatedAt]
    )

    override suspend fun getAlbumById(id: String): Album = dbQuery {
        try {
            AlbumesTable
                .selectAll()
                .where { AlbumesTable.id eq UUID.fromString(id) }
                .map { it.toAlbum() }
                .singleOrNull()
                ?: throw NoSuchElementException("No se encontró el album $id")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID inválido $id")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllAlbums(): List<Album> = dbQuery {
        AlbumesTable
            .selectAll()
            .orderBy(AlbumesTable.releaseYear to SortOrder.DESC)

            .map { it.toAlbum() }
    }

    override suspend fun getAlbumsByArtist(artistId: String): List<Album> = dbQuery {
        try {
            val uuid = UUID.fromString(artistId)


            ArtistsTable
                .selectAll()
                .where { ArtistsTable.id eq uuid }
                .singleOrNull()
                ?: throw NoSuchElementException("No se encontró al artista $artistId")

            AlbumesTable
                .selectAll()
                .where { AlbumesTable.artistId eq uuid }
                .orderBy(AlbumesTable.releaseYear to SortOrder.DESC)
                .map { it.toAlbum() }

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID de artista inválido: $artistId")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createAlbum(album: Album): Album = dbQuery {
        try {

            ArtistsTable
                .selectAll()
                .where { ArtistsTable.id eq album.artistId }
                .singleOrNull()
                ?: throw NoSuchElementException("No se encontró al artista ${album.artistId}")

            val now = OffsetDateTime.now()

            AlbumesTable.insert {
                it[id] = album.id
                it[title] = album.title
                it[releaseYear] = album.releaseYear
                it[artistId] = album.artistId
                it[createdAt] = now
                it[updatedAt] = now
            }

            album.copy(createdAt = now, updatedAt = now)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateAlbum(id: String, album: Album): Album = dbQuery {
        try {
            val uuid = UUID.fromString(id)


            AlbumesTable
                .selectAll()
                .where { AlbumesTable.id eq uuid }
                .singleOrNull()
                ?: throw NoSuchElementException("No se encontró al album: $id")

            val now = OffsetDateTime.now()

            AlbumesTable.update({ AlbumesTable.id eq uuid }) {
                it[title] = album.title
                it[releaseYear] = album.releaseYear
                it[updatedAt] = now
            }


            AlbumesTable
                .selectAll()
                .where { AlbumesTable.id eq uuid }
                .map { it.toAlbum() }
                .single()

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID inválido: $id ")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAlbum(id: String): Boolean = dbQuery {
        try {
            val uuid = UUID.fromString(id)


            AlbumesTable
                .selectAll()
                .where { AlbumesTable.id eq uuid }
                .singleOrNull()
                ?: throw NoSuchElementException("No se encontró al album $id")

            val deletedRows = AlbumesTable.deleteWhere { AlbumesTable.id eq uuid }
            deletedRows > 0

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID inválido $id")
        } catch (e: Exception) {
            throw e
        }
    }
}