import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
}

group = "org.socialculture.platform"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val snippetsDir = file("build/generated-snippets")

// asciidoctorExt 설정
val asciidoctorExt = "asciidoctorExt"
configurations.create(asciidoctorExt) {
    extendsFrom(configurations.testImplementation.get())
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // QueryDSL 추가
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    // Lombok 의존성 추가
    compileOnly("org.projectlombok:lombok")
    kapt("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Asciidoctor 관련 의존성
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor:3.0.1")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc:3.0.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.google.code.gson:gson:2.8.9")

    // Mockito 의존성 추가
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")

    // JWT 의존성 추가
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")

    // WebFlux 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // 이메일 인증 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-mail:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation("commons-net:commons-net:3.9.0")

    // Redis 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Embedded Redis 의존성 추가
    implementation("it.ozimov:embedded-redis:0.7.2")

    // Kotlin-reflect 의존성 추가
    implementation("org.jetbrains.kotlin:kotlin-reflect")

}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kapt {
    correctErrorTypes = true
    generateStubs = true
}

tasks.named<Delete>("clean") {
    delete(file("$buildDir/generated"))
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(file("$buildDir/generated"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.named<Test>("test") {
    outputs.dir(snippetsDir)
    useJUnitPlatform()
    finalizedBy("asciidoctor")
}

tasks.named<AsciidoctorTask>("asciidoctor") {
    inputs.dir(snippetsDir)
    configurations(asciidoctorExt)
    dependsOn(tasks.named("test"))
}

tasks.named<BootJar>("bootJar") {
    dependsOn("asciidoctor")
    from(tasks.named<AsciidoctorTask>("asciidoctor").get().outputDir) {
        into("BOOT-INF/classes/static/docs")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

kotlin.sourceSets["main"].kotlin.srcDir("$buildDir/generated/source/kapt/main")
