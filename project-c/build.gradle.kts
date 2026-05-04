plugins {
    application
    java
}

group = "com.example.c"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.example.c.App")
}

dependencies {
    // CVE-2018-10237 — Guava unbounded memory allocation
    implementation("com.google.guava:guava:24.1-jre")

    // CVE-2021-39139 (and many others) — XStream RCE via untrusted XML
    implementation("com.thoughtworks.xstream:xstream:1.4.17")

    // CVE-2022-23221 / CVE-2021-42392 — H2 console RCE
    implementation("com.h2database:h2:1.4.199")

    // CVE-2020-9488 — log4j 1.x SMTP appender SSL hostname verification
    implementation("log4j:log4j:1.2.17")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}
