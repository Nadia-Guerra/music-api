package com.example

import com.example.infrastructure.di.AppModule
import com.example.plugins.configureSerialization
import com.example.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.koin.ktor.plugin.Koin
import plugins.DatabaseFactory
import plugins.configureMonitoring


fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureRouting(AppModule())
    configureMonitoring()
}
