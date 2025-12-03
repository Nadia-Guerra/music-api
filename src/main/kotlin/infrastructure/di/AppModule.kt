package com.example.infrastructure.di

import com.example.application.usecases.ArtistUseCases
import com.example.application.usecases.AlbumUseCase
import com.example.domain.interfaces.TrackInterface
import com.example.application.usecases.TracksUseCase
import com.example.infrastructure.repositories.TrackRepositoryImpl

import com.example.domain.interfaces.ArtistInterface
import com.example.domain.interfaces.AlbumInterface
import com.example.infrastructure.repositories.ArtistRepositoryImpl
import com.example.infrastructure.repositories.AlbumRepositoryImpl

import org.koin.dsl.module //mejor no usar koin no le entendi

class AppModule() {

    val artistRepository: ArtistInterface = ArtistRepositoryImpl()
     val albumRepository: AlbumInterface = AlbumRepositoryImpl()
    val trackRepository: TrackInterface = TrackRepositoryImpl()
    val artistUseCase = ArtistUseCases(artistRepository)
    val albumUseCase = AlbumUseCase(albumRepository, artistRepository)
    val tracksUseCase = TracksUseCase(trackRepository, albumRepository, artistRepository)
}

