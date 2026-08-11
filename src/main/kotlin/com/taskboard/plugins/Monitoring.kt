package com.taskboard.plugins

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { java.util.UUID.randomUUID().toString() }
        verify { it.isNotBlank() }
    }

    install(CallLogging) {
        level = Level.INFO
        callIdMdc("call-id")
    }
}
