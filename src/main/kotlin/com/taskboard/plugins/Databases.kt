package com.taskboard.plugins

import com.taskboard.config.databaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.log
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    val dbConfig = databaseConfig()

    val dataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = dbConfig.url
            username = dbConfig.user
            password = dbConfig.password
            maximumPoolSize = dbConfig.maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        },
    )

    // Flyway = schema migrations (как Doctrine/Laravel migrations).
    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
        .migrate()

    // Exposed использует тот же DataSource (пул соединений HikariCP).
    Database.connect(dataSource)
    log.info("Database connected: {}", dbConfig.url.substringBefore("?"))

    monitor.subscribe(ApplicationStopping) {
        dataSource.close()
        log.info("Database connection pool closed")
    }
}
