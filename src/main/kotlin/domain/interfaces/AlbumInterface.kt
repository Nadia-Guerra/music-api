package com.example.domain.interfaces

import com.example.domain.models.Album
import com.example.domain.models.Artist

interface AlbumInterface {
    suspend fun getAllAlbums(): List<Album>
    suspend fun getAlbumById(id: String): Album
    suspend fun getAlbumsByArtist(artistId: String): List<Album>
    suspend fun updateAlbum(id: String, album: Album): Album
    suspend fun createAlbum(album: Album): Album
    suspend fun deleteAlbum(id: String): Boolean
}