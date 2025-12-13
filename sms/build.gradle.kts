plugins {
    java
    application
}

group = "com.nimdec.sms"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.twilio.sdk:twilio:10.8.0")

    testImplementation(libs.junit.jupiter)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "com.nimdec.sms"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
