package com.taskboard

import com.taskboard.di.configureDependencies
import com.taskboard.plugins.configureAuthentication
import com.taskboard.plugins.configureDatabases
import com.taskboard.plugins.configureHTTP
import com.taskboard.plugins.configureMonitoring
import com.taskboard.plugins.configureRouting
import com.taskboard.plugins.configureSerialization
import com.taskboard.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureMonitoring()
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    configureDatabases()
    configureDependencies()
    configureAuthentication()
    configureRouting()
}
