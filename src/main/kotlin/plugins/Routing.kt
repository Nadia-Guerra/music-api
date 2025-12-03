package com.example.plugins

import com.example.infrastructure.di.AppModule
import com.example.presentation.routes.artistRoutes
import com.example.presentation.routes.albumRoutes
import com.example.presentation.routes.trackRoutes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Application.configureRouting(appModule: AppModule) {
    routing {
        route("/api") {
            artistRoutes(appModule.artistUseCase)
            albumRoutes(appModule.albumUseCase)
            trackRoutes(appModule.tracksUseCase)
        }
    }
}
