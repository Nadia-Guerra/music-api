package com.example.presentation.routes

import com.example.application.dto.createTrackRequest
import com.example.application.dto.updateTrackRequest
import com.example.application.usecases.TracksUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*


fun Route.trackRoutes(
    tracksUseCase: TracksUseCase
) {

    route("/tracks") {
        get {
            try {
                val tracks = tracksUseCase.getAllTracks()
                call.respond(HttpStatusCode.OK, tracks)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error obteniendo tracks"))
            }
        }

        get("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val response = tracksUseCase.getTrack(id)
                call.respond(HttpStatusCode.OK, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Crear nuevo track
        post {
            try {
                val request = call.receive<createTrackRequest>()

                if (request.title.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Título requerido"))
                    return@post
                }

                if (request.duration <= 0) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Duración debe ser mayor a 0"))
                    return@post
                }

                if (request.duration > 7200) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Duración máxima 2 horas"))
                    return@post
                }

                val response = tracksUseCase.createTrack(request)
                call.respond(HttpStatusCode.Created, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error creando track"))
            }
        }

        put("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val request = call.receive<updateTrackRequest>()

                if (request.title == null && request.duration == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Proporcione al menos un campo"))
                    return@put
                }

                if (request.title != null && request.title.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Título no puede estar vacío"))
                    return@put
                }

                if (request.duration != null && request.duration <= 0) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Duración debe ser mayor a 0"))
                    return@put
                }

                if (request.duration != null && request.duration > 7200) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Duración máxima 2 horas"))
                    return@put
                }

                val response = tracksUseCase.updateTrack(id, request)
                call.respond(HttpStatusCode.OK, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error actualizando track"))
            }
        }


        delete("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val deleted = tracksUseCase.deleteTrack(id)

                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Track eliminado"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "No se pudo eliminar"))
                }

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error eliminando track"))
            }
        }
    }

    // tracks por album
    route("/albumes/{albumId}/tracks") {
        get {
            try {
                val albumId = call.parameters["albumId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de álbum requerido"))

                val tracks = tracksUseCase.getTracksByAlbum(albumId)
                call.respond(HttpStatusCode.OK, tracks)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error obteniendo tracks"))
            }
        }
    }
}
