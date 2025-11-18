plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.21"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.metalshader"
version = project.findProperty("pluginVersion") as String? ?: "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

intellij {
    version.set("2024.2.5")
    type.set("IC")
    plugins.set(listOf())
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("")
        version.set(project.version.toString())
        
        // Convert version to release version format: 1.0.10 -> 10010
        val versionParts = project.version.toString().split(".")
        val releaseVersion = String.format("%d%02d%02d", 
            versionParts[0].toInt(),
            versionParts[1].toInt(),
            versionParts[2].toInt()
        )
        changeNotes.set("""
            <h3>Version ${project.version} (November 2025)</h3>
            <ul>
              <li><b>FIXED:</b> Deprecated API usage - Updated to modern IntelliJ Platform APIs</li>
              <li><b>FIXED:</b> Template context type now uses new API</li>
              <li><b>FIXED:</b> Live templates provider updated to recommended API</li>
              <li><b>IMPROVED:</b> Better compatibility with IntelliJ IDEA 2024.2+</li>
            </ul>
        """.trimIndent())
        
        pluginDescription.set("""
            <h1>Metal Shader Support</h1>
            <p>Comprehensive Metal Shading Language support for IntelliJ-based IDEs.</p>
            
            <h2>Features</h2>
            <ul>
                <li>Syntax highlighting for Metal Shading Language 3.2</li>
                <li>Code completion for Metal stdlib functions</li>
                <li>Error detection and linting</li>
                <li>Template snippets for common shader patterns</li>
                <li>Semantic highlighting for keywords, attributes, and built-in types</li>
            </ul>
            
            <h2>Supported File Extensions</h2>
            <ul>
                <li>.metal - Metal shader files</li>
                <li>.msl - Metal Shading Language files</li>
            </ul>
            
            <p>Perfect for iOS/macOS game development, GPU compute, and video processing.</p>
        """.trimIndent())
        
        changeNotes.set("""
            <h3>0.1.0</h3>
            <ul>
                <li>Initial release</li>
                <li>Basic Metal syntax highlighting</li>
                <li>Code completion support</li>
                <li>Error detection</li>
                <li>Support for IntelliJ IDEA 2024.1-2025.3</li>
            </ul>
        """.trimIndent())
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        val publishToken = providers.gradleProperty("token")
            .orElse(providers.environmentVariable("PUBLISH_TOKEN"))
        token.set(publishToken)
    }

    test {
        useJUnitPlatform()
    }
}
