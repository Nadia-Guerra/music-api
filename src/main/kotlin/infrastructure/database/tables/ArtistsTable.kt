package com.example.infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone


//deben de ir con los mismos nombres y especificaciones de la bd
object ArtistsTable: Table("artistas") {
    val id = uuid("id")
    val name = varchar("name", 100)
    val genre = varchar("genre", 50).nullable()
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}