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
            <h3>Version ${project.version} (November 2025) 🚀</h3>
            <p><b>Professional Metal Shading Language support for Apple platform development</b></p>
            
            <h4>✨ Core Features</h4>
            <ul>
                <li><b>Full Metal 3.2 & 4.0 Language Support</b> - Complete syntax highlighting for all Metal versions</li>
                <li><b>Smart Code Completion</b> - 300+ built-in functions, types, and keywords with documentation</li>
                <li><b>Metal 4.0 Raytracing</b> - Hardware-accelerated raytracing with acceleration structures (iOS 18+, macOS 15+)</li>
                <li><b>Metal 4.0 Mesh Shaders</b> - Object and mesh shader support for GPU-driven rendering</li>
                <li><b>Advanced Error Detection</b> - Real-time syntax validation and semantic analysis</li>
                <li><b>Performance Optimization</b> - Live hints for expensive operations, thread divergence, and memory access patterns</li>
                <li><b>MPS Integration</b> - Metal Performance Shaders auto-completion (50+ classes)</li>
            </ul>
            
            <h4>🎯 Language Features</h4>
            <ul>
                <li>Semantic highlighting for keywords, attributes, and built-in types</li>
                <li>Code folding for functions, structs, and blocks</li>
                <li>Structure view with function and type navigation</li>
                <li>Find usages and refactoring support</li>
                <li>Comment/uncomment and smart brace matching</li>
                <li>10 live templates for rapid shader development</li>
            </ul>
            
            <h4>📦 File Support</h4>
            <ul>
                <li>.metal - Metal shader files</li>
                <li>.msl - Metal Shading Language files</li>
                <li>.mm - Objective-C++ with Metal</li>
                <li>.h, .hpp - Metal header files</li>
            </ul>
            
            <h4>⚡ Pro Features (Freemium)</h4>
            <ul>
                <li><b>Shader Complexity Analyzer</b> - GPU cycle estimation and bottleneck detection</li>
                <li><b>Advanced Validator</b> - Comprehensive shader validation beyond syntax</li>
                <li><b>Live Preview</b> - Real-time shader rendering at 60 FPS</li>
                <li><b>GPU Profiler</b> - Performance charts and analysis</li>
                <li><b>Debug Panel</b> - Visual debugging with variable inspection</li>
            </ul>
            
            <h4>🏢 Enterprise Features</h4>
            <ul>
                <li><b>Cross-Platform Transpiler</b> - Convert Metal → SPIR-V/HLSL/WGSL</li>
            </ul>
            
            <h4>📚 Includes Examples</h4>
            <ul>
                <li>1,500+ lines of production-ready shader code</li>
                <li>Vertex/fragment shaders for graphics rendering</li>
                <li>Compute kernels with threadgroup memory</li>
                <li>Metal 4 raytracing (path tracing, shadows, ambient occlusion)</li>
                <li>Metal 4 mesh shaders (LOD, culling, procedural geometry)</li>
            </ul>
            
            <p><b>Perfect for:</b> Game developers, GPU compute engineers, graphics programmers, Vision Pro developers</p>
            <p><i>© 2025 Jakub Jirák. Contact: support@jakubjirak.com</i></p>
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
