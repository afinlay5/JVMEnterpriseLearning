plugins {
    java
    application
}

group = "com.nimdec"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("org.apache.commons:commons-lang3:3.14.0")

    // Keep Jackson modules aligned
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.20.1"))
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("com.nimdec.Main")
}

tasks.test {
    useJUnitPlatform()
}
