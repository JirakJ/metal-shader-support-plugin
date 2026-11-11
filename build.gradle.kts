plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij.platform)
    alias(libs.plugins.detekt)
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(providers.gradleProperty("javaVersion").get().toInt())
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion"),
        )
        pluginVerifier()
        zipSigner()
    }

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

// Exclude IDE-provided libraries from plugin distribution
configurations {
    runtimeClasspath {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains", module = "annotations")
    }
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        // id (com.jakubjirak.metalshader), name and description come from plugin.xml.
        version = providers.gradleProperty("pluginVersion")

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = provider { null }
        }

        changeNotes = """
            <h3>2026.9.0</h3>
            <ul>
                <li>The Metal file type only claims .metal and .msl (no longer C/C++/Objective-C headers and .mm files).</li>
                <li>since-build raised to 242 to match the Java 21 / 2024.2 build.</li>
                <li>Performance hints match whole keywords.</li>
                <li>Removed preview, debugger and profiler windows that showed fixed numbers, and the unused settings page.</li>
            </ul>
            <h3>2026.8.0</h3>
            <ul>
                <li>The plugin is now completely free — all features unlocked, no license needed.</li>
                <li>Source code is now open source under Apache-2.0: https://github.com/JirakJ/metal-shader-support-plugin</li>
            </ul>
            <h3>2026.4.2</h3>
            <ul>
                <li>IntelliJ Platform internal-API compliance cleanup (live templates provider)</li>
            </ul>
        """.trimIndent()
    }

    pluginVerification {
        ides {
            ide("IC", "2024.2.5")
            ide("IC", "2024.3")
            ide("IC", "2025.1.3")
        }
    }

    signing {
        // Configure via environment variables:
        // ORG_GRADLE_PROJECT_signCertificateChain
        // ORG_GRADLE_PROJECT_signPrivateKey
        // ORG_GRADLE_PROJECT_signPassword
    }

    publishing {
        token = providers.environmentVariable("PLUGIN_TOKEN")
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        gradleVersion = "8.12"
    }
}

detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
    // Baseline = size/complexity of the hand-written lexer state machine and of the large rule tables
    // (validator, optimizer, profiler, documentation, snippets, transpiler); splitting them is a risky refactor.
    baseline = file("detekt-baseline.xml")
}
