package app.mobile.tasktracker.plugins

import org.jetbrains.exposed.sql.Database

fun configureDatabase() {
    Database.connect(
        url = "jdbc:postgresql://10.0.2.2:5432/mobile-app-local",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )
}
