plugins {
    java
    application

    id("com.diffplug.spotless") version "7.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springframework.boot") version "3.4.4"
}

group = "com.nimdec"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation(libs.junit.jupiter)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "com.nimdec.RESTApi"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
