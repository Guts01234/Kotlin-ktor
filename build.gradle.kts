plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "com.taskboard"
version = "0.1.0"

application {
    mainClass.set("com.taskboard.ApplicationKt")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.callId)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.defaultHeaders)
    implementation(ktorLibs.server.compression)
    implementation(ktorLibs.server.doubleReceive)

    implementation(libs.logback.classic)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(libs.kotlin.test.junit5)
}

tasks.test {
    useJUnitPlatform()
}
