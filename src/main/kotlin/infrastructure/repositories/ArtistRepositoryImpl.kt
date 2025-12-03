package com.example.infrastructure.repositories

import com.example.domain.interfaces.ArtistInterface
import com.example.domain.models.Artist
import com.example.infrastructure.database.tables.ArtistsTable
import com.example.infrastructure.database.tables.ArtistsTable.genre
import com.example.infrastructure.database.tables.ArtistsTable.name
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import plugins.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SortOrder
import java.time.OffsetDateTime
import org.jetbrains.exposed.sql.*
import java.util.UUID


class ArtistRepositoryImpl: ArtistInterface {

    private fun ResultRow.toArtist() = Artist(
        id = this[ArtistsTable.id],
        name = this[ArtistsTable.name],
        genre = this[ArtistsTable.genre],
        createdAt = this[ArtistsTable.createdAt],
        updatedAt = this[ArtistsTable.updatedAt]
    )

    override suspend fun getArtistById(id: String): Artist = dbQuery {
        try{
            ArtistsTable
                .selectAll()
                .where{ArtistsTable.id eq UUID.fromString(id)}
                .map{it.toArtist()}
                .singleOrNull()
                ?:throw NoSuchElementException("No se encontró al artista $id")

        }catch (e:Exception){
            throw e
        }

    }

    override suspend fun getAllArtists(): List<Artist> = dbQuery {
        ArtistsTable
            .selectAll()
            .orderBy(ArtistsTable.name to SortOrder.ASC)
            .map{it.toArtist()}

    }


    override suspend fun createArtist(artist: Artist): Artist = dbQuery {
        val time = OffsetDateTime.now()

        ArtistsTable.insert{
            it[id]=artist.id
            it[name] = artist.name
            it[genre] = artist.genre
            it[createdAt] = time
            it[updatedAt] = time
        }

        artist.copy(createdAt = time, updatedAt = time)
    }

    override suspend fun updateArtist(id: String, artist: Artist): Artist = dbQuery {
        try{
            val uuid = UUID.fromString(id)

            val exists = ArtistsTable
                .selectAll()
                .where{ ArtistsTable.id eq uuid}
                .singleOrNull()
                ?:throw NoSuchElementException("No se encontró al artista $id")

            val time = OffsetDateTime.now()

            ArtistsTable.update({ArtistsTable.id eq uuid}){
                artist.name.let{name -> it[ArtistsTable.name] = name}
                it[genre] = artist.genre
                it[updatedAt] = time
            }

            ArtistsTable
                .selectAll()
                .where{ArtistsTable.id eq uuid}
                .map{it.toArtist()}
                .single()
        }catch (e: IllegalArgumentException){
            throw IllegalArgumentException("$id no es un UUID válido")
        }catch (e:Exception){
            throw e
        }

    }
    override suspend fun deleteArtist(id: String): Boolean = dbQuery {
        try{
            val uuid = UUID.fromString(id)

            val exists = ArtistsTable
                .selectAll()
                .where{ ArtistsTable.id eq uuid }
                .singleOrNull()
                ?: throw NoSuchElementException("No se encontro al artista $id")


            val deletedRows = ArtistsTable.deleteWhere { ArtistsTable.id eq uuid }


            deletedRows > 0
        } catch(e: IllegalArgumentException){
            throw IllegalArgumentException("$id no es un UUID válido")
        } catch (e:Exception){
            throw e
        }
    }
}