plugins {
    // Kotlin — template now fully Kotlin
    kotlin("jvm") version "2.0.21"
    // IDEA — generates .idea/.iml via `gradle idea`, helps JetBrains import
    idea
    // Formatter — Spotless + ktlint for Kotlin (nix fmt via flake.nix)
    id("com.diffplug.spotless") version "7.0.2"
    // Shadow removed — manual fatJar used to avoid ASM 65 issue (shadow 8.1.1 can't read Java 21).
    // If you want relocation, add org.gradle.shadow 8.3.x + re-enable relocate block below.
}

group = "gay.nyaa.purrskills"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

val paperVersion = findProperty("paperVersion") as String? ?: "1.21.10-R0.1-SNAPSHOT" // target 1.26.2 when released → ./gradlew -PpaperVersion=1.26.2-R0.1-SNAPSHOT build

dependencies {
    // Paper 1.26.2 target — defaults to 1.21.10 until 1.26.2 hits repo.papermc.io (API compat same)
    compileOnly("io.papermc.paper:paper-api:$paperVersion")

    // PurrCore — provides shared Database (HikariCP) + I18n
    compileOnly(files("../PurrCore/build/libs/purrcore-1.0.0.jar"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Tests — JUnit5 + MockK (Kotlin native where possible, JVM for Paper API mocks)
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation("io.papermc.paper:paper-api:$paperVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation(files("../PurrCore/build/libs/purrcore-1.0.0.jar"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.processResources {
    filteringCharset = "UTF-8"
}

tasks.jar {
    archiveBaseName.set("purrskills")
}

// Manual fatJar — bundles runtimeClasspath WITHOUT Kotlin (provided by PurrCore to avoid classloader conflicts).
// PurrCore already includes Kotlin stdlib, so PurrSkills must NOT bundle it.
val shadowJar by tasks.registering(Jar::class) {
    archiveBaseName.set("purrskills")
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .filterNot { it.name.startsWith("kotlin-stdlib") }      // Exclude Kotlin stdlib
            .filterNot { it.name.startsWith("kotlin-reflect") }     // Exclude Kotlin reflect
            .filterNot { it.name.startsWith("kotlinx-") }           // Exclude Kotlinx libs
            .map { zipTree(it) }
    })
    // merge service files (e.g., sqlite jdbc) — naive: exclude duplicates already
}

tasks.build {
    dependsOn(shadowJar)
}

// IDEA config — mark JDK 21, Kotlin, resources
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        // exclude build dirs
        excludeDirs.addAll(files(".gradle", "build", "out", ".idea/workspace.xml", ".idea/tasks.xml"))
    }
}

// Formatter — Spotless (ktlint for Kotlin, trim for misc)
spotless {
    // Kotlin — ktlint 1.5.0 (supports Kotlin 2.0/2.1, official style) — native ktlint via pkgs.ktlint also available
    kotlin {
        target("src/**/*.kt")
        ktlint("1.5.0").editorConfigOverride(
            mapOf(
                "indent_size" to "4",
                "continuation_indent_size" to "4",
                "max_line_length" to "off",
                "ktlint_standard_max-line-length" to "disabled",
                "ktlint_standard_no-wildcard-imports" to "disabled",
            ),
        )
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.kts", "gradle/*.kts")
        ktlint("1.5.0").editorConfigOverride(
            mapOf(
                "ktlint_standard_max-line-length" to "disabled",
            ),
        )
        trimTrailingWhitespace()
        endWithNewline()
    }
    // Misc — yaml/md/json: trim + newline (no reformat)
    format("misc") {
        target("*.md", "*.yml", "*.yaml", "*.json", ".editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
        leadingTabsToSpaces(2)
    }
}

// Tests — JUnit5 + MockK + tag-based filtering
tasks.test {
    useJUnitPlatform {
        // Exclude integration tests (require Bukkit server initialization)
        excludeTags("integration")
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}
