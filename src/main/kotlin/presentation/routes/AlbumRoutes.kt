package com.example.presentation.routes

import com.example.application.dto.createAlbumRequest
import com.example.application.dto.updateAlbumRequest
import com.example.application.usecases.AlbumUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*


fun Route.albumRoutes(albumUseCase: AlbumUseCase) {

    route("/albumes") {
        get {
            val albums = albumUseCase.getAllAlbums()
            call.respond(HttpStatusCode.OK, albums)
        }


        get("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val response = albumUseCase.getAlbumById(id)
                call.respond(HttpStatusCode.OK, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }


        post {
            try {
                val request = call.receive<createAlbumRequest>()

                if (request.title.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Título requerido"))
                    return@post
                }

                if (request.releaseYear < 1900 || request.releaseYear > 2100) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Año inválido"))
                    return@post
                }

                val response = albumUseCase.createAlbum(request)
                call.respond(HttpStatusCode.Created, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al crear álbum"))
            }
        }


        put("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val request = call.receive<updateAlbumRequest>()

                if (request.title == null && request.releaseYear == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Proporcione al menos un campo"))
                    return@put
                }

                if (request.title != null && request.title.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Título no puede estar vacío"))
                    return@put
                }

                if (request.releaseYear != null && (request.releaseYear < 1900 || request.releaseYear > 2100)) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Año inválido"))
                    return@put
                }

                val response = albumUseCase.updateAlbum(id, request)
                call.respond(HttpStatusCode.OK, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al actualizar"))
            }
        }


        delete("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val deleted = albumUseCase.deleteAlbum(id)

                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Álbum eliminado"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "No se pudo eliminar"))
                }

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al eliminar"))
            }
        }

    }

    route("/artistas/{artistId}/albumes") {
        get {
            try {
                val artistId = call.parameters["artistId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de artista requerido"))

                val albums = albumUseCase.getAlbumsByArtist(artistId)
                call.respond(HttpStatusCode.OK, albums)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error obteniendo álbumes"))
            }
        }
    }
}
