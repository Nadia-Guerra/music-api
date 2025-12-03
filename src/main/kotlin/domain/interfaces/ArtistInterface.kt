package com.example.domain.interfaces

import com.example.domain.models.Artist

interface ArtistInterface {
    suspend fun getArtistById(id: String): Artist
    suspend fun getAllArtists(): List<Artist>
    suspend fun createArtist(artist: Artist): Artist
    suspend fun updateArtist(id:String,artist: Artist):Artist
    suspend fun deleteArtist(id: String): Boolean
}

//artist es la variable
//Artist es la lista de artistas