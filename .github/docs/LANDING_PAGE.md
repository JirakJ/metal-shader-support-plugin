# Metal Shader Support

## Professional Metal Shading Language Support for JetBrains IDEs

> Build high-performance GPU shaders with full Metal 3.2 and 4.0 support — syntax highlighting, code completion, live preview, and GPU profiling.

---

## The Problem

Metal shader development has been locked inside Xcode with limited editor features. Developers writing GPU shaders for iOS, macOS, and visionOS deserve the same productivity tools they get for Swift and Objective-C — smart code completion, real-time error detection, performance analysis, and live preview. Until now, Metal shaders were second-class citizens in the development workflow.

---

## Key Features

### Metal 3.2 and 4.0 Syntax Highlighting
Full support for the latest Metal specifications including raytracing, mesh shaders, and all Metal Shading Language keywords, types, attributes, and built-in functions.

### Smart Code Completion
300+ documented keywords, types, and functions with context-aware completion. Get suggestions for Metal stdlib functions, attribute parameters, and shader templates as you type.

### Real-Time Error Detection
Catch mistakes as you code. The plugin validates threadgroup sizes (max 1024 threads), texture access bounds, attribute formats, and vector swizzle patterns in real time.

### Performance Analyzer
Get shader complexity scoring with specific optimization hints. The analyzer detects expensive operations (pow, sqrt, division), thread divergence, and texture access patterns that impact performance.

### Live Shader Preview
See your shaders rendered in real-time at 60 FPS in a dedicated tool window. Edit your shader code and watch the output update instantly.

### GPU Profiler
Estimate GPU cycles, identify bottlenecks, and view performance charts. Profile shader execution to find and fix performance problems.

### Metal Performance Shaders (MPS)
Code completion and framework support for Metal Performance Shaders — Apple's optimized library for image processing, neural networks, and matrix operations.

### Cross-Platform Transpiler
Convert Metal shaders to SPIR-V, HLSL, and WGSL formats for cross-platform compatibility. Target Vulkan, DirectX, and WebGPU from your Metal source code.

---

## How It Works

1. **Install the plugin** — Search for "Metal Shader Support" in the JetBrains Marketplace.
2. **Create a .metal file** — Start writing Metal shader code with full syntax highlighting and code completion.
3. **Use live templates** — Type `vertex`, `fragment`, or `kernel` and press Tab to generate shader templates instantly.
4. **Analyze and optimize** — Use the performance analyzer and GPU profiler to find and fix performance bottlenecks.

---

## Pricing

### Free Tier — $0
- Metal 3.2 and 4.0 syntax highlighting
- Code completion with 300+ keywords
- Real-time error detection
- Comment/uncomment, brace matching
- 10 live templates
- Code folding and formatting
- Structure view and navigation

### Pro — $49/year
- Live shader preview (60 FPS)
- Shader debugger integration
- GPU profiler with cycle estimation
- Performance analyzer with optimization hints
- Shader optimizer with one-click apply
- Debug helpers and texture visualizers

### Enterprise — $199/year
- Cross-platform transpiler (Metal → SPIR-V/HLSL/WGSL)
- Team collaboration features
- CI/CD integration
- Advanced analytics

---

## Testimonials

> *"Metal Shader Support is the missing piece for GPU development outside Xcode. The code completion alone makes it worth installing."*
> — **iOS Game Developer**

> *"The performance analyzer found three expensive operations in our compute shader that we had missed for months."*
> — **Graphics Programmer, Game Studio**

> *"Live shader preview at 60 FPS in the IDE — this is how shader development should be."*
> — **Technical Artist, VR Company**

---

## FAQ

**Q: Do I need a Mac to use this plugin?**
A: macOS is recommended since Metal is an Apple-only framework. The syntax highlighting and code completion work on any platform, but compilation and preview require macOS.

**Q: Which file types are supported?**
A: `.metal` and `.msl` files are fully supported with dedicated file type recognition.

**Q: Does it support Metal 4.0 features?**
A: Yes. The plugin supports Metal 4.0 features including raytracing (acceleration structures, ray queries), mesh shaders, and 40+ new raytracing functions.

**Q: Which JetBrains IDEs are supported?**
A: IntelliJ IDEA 2024.1+, CLion 2024.1+, and AppCode 2024.1+.

**Q: Can I transpile Metal shaders to other formats?**
A: Yes. The Enterprise tier includes a cross-platform transpiler that converts Metal shaders to SPIR-V, HLSL, and WGSL.

---

## Get Started

Install Metal Shader Support and bring first-class Metal development to your JetBrains IDE.

[Install from Marketplace](https://plugins.jetbrains.com) | [Documentation](https://github.com/yourusername/metal-shader-support-plugin/wiki)
