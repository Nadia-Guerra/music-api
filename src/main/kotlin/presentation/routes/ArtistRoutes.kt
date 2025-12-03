package com.example.presentation.routes
import io.ktor.server.routing.Route
import com.example.application.usecases.ArtistUseCases
import com.example.application.dto.createArtistRequest
import com.example.application.dto.updateArtistRequest
import com.example.domain.models.Artist
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.request.receive
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject



fun Route.artistRoutes(artistUseCase: ArtistUseCases) {
    route("/artistas") {
        post {
            try {
                val request = call.receive<createArtistRequest>()

                if (request.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El nombre es requerido"))
                    return@post
                }
                val response = artistUseCase.createArtist(request)
                call.respond(HttpStatusCode.Created, response)

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al crear artista ${e.message}")
                )
            }
        }

        get {
            try {
                val artists = artistUseCase.getAllArtists()
                call.respond(HttpStatusCode.OK, artists)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "No se pudo obtener al artista ${e.message}")
                )
            }
        }


        get("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Error no parameter"))

                val response = artistUseCase.getArtistById(id)
                call.respond(HttpStatusCode.OK, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "No se pudo obtener al artista ${e.message}")
                )
            }
        }

        put("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val request = call.receive<updateArtistRequest>()

                if (request.name == null && request.genre == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Es necesario un campo apra actualizar"))
                    return@put
                }

                if (request.name != null && request.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Inserte un nombre valido"))
                    return@put
                }

                val response = artistUseCase.updateArtist(id, request)
                call.respond(HttpStatusCode.OK, response)

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al actualizar artista ${e.message}")
                )
            }
        }

        delete("/{id}") {
            try {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID requerido"))

                val deleted = artistUseCase.deleteArtist(id)

                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Artista eliminado"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "No se pudo eliminar el artista"))
                }

            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al artista ${e.message}")
                )
            }
        }
    }
}