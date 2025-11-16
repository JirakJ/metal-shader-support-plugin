# Metal Shader Support Plugin - Installation Guide

## Quick Start

### 1. Install the Plugin

**Option A: From ZIP File (Recommended)**
```
1. Download: build/distributions/metal-shader-support-plugin-1.0.8.zip
2. Open IntelliJ IDEA (2024.2 or newer)
3. Go to: Settings/Preferences → Plugins
4. Click ⚙️ (gear icon) → Install Plugin from Disk...
5. Select the ZIP file
6. Click OK
7. Restart IDE
```

**Option B: Build from Source**
```bash
cd /Users/jirakj/work/metal-shader-support-plugin
./gradlew clean buildPlugin
# Plugin ZIP will be in: build/distributions/
```

### 2. Verify Installation

1. Open or create a `.metal` file
2. You should see syntax highlighting automatically
3. Type `texture.` to trigger code completion
4. Check menu: Tools → Metal Shader Support

### 3. Activate License (Optional)

**Free Tier**
- No license needed! All basic features work out of the box

**Pro Tier ($49/year)**
```
1. Tools → Metal Shader Support → License → Activate License
2. Enter: METAL-PRO-1234-5678-ABCD
3. Click "Activate"
4. See pro-license.txt for details
```

**Enterprise Tier ($199/year)**
```
1. Tools → Metal Shader Support → License → Activate License
2. Enter: METAL-ENT-9999-8888-WXYZ
3. Click "Activate"  
4. See enterprise-license.txt for details
```

## Feature Overview

### ✅ Free Features (Always Available)

- **Syntax Highlighting**
  - Metal 3.2 and Metal 4.0 keywords
  - Attributes `[[...]]`
  - Built-in types and functions
  - Address spaces (device, constant, threadgroup)

- **Code Completion**
  - 300+ keywords and functions
  - Metal Performance Shaders (MPS)
  - Smart attribute suggestions
  - Function signatures

- **Error Detection**
  - Real-time syntax validation
  - Type checking
  - Common mistake detection

- **Code Templates**
  - `kernel` - Compute kernel template
  - `vertex` - Vertex shader template
  - `fragment` - Fragment shader template
  - `vpfrag` - Vertex + Fragment pair

- **Performance Analysis**
  - Complexity analyzer
  - Optimization hints
  - Memory warnings

### 🔒 Pro Features (Requires Pro License)

- **Live Shader Preview**
  - Real-time rendering
  - Hot-reload on code changes
  - FPS counter
  - Performance metrics

- **GPU Profiler**
  - Cycle estimation
  - Memory bandwidth
  - Occupancy calculator
  - Bottleneck detection

- **Advanced Validator**
  - Beyond syntax checking
  - GPU compatibility
  - Best practices

- **Shader Optimizer**
  - Auto-vectorization
  - Code simplification
  - One-click fixes

- **Debug Helper**
  - Texture visualizers
  - Assert helpers
  - Debug overlays

### 🔐 Enterprise Features (Requires Enterprise License)

- **Shader Transpiler**
  - Metal → SPIR-V (Vulkan)
  - Metal → HLSL (DirectX)
  - Metal → WGSL (WebGPU)
  - One-click conversion

- **Team Features**
  - Shader library management
  - Performance benchmarks
  - CI/CD integration

## File Type Support

The plugin recognizes these extensions:
- `.metal` - Primary Metal shader files
- `.msl` - Metal Shading Language files
- `.mm` - Objective-C++ files with Metal code
- `.h` - Header files (Metal C++)
- `.hpp` - C++ header files (Metal C++)

## Keyboard Shortcuts

| Action | Shortcut | Description |
|--------|----------|-------------|
| Complete Code | Ctrl+Space | Trigger code completion |
| Show Documentation | Ctrl+Q | Show quick documentation |
| Analyze Complexity | Tools menu | Analyze shader performance |
| Format Code | Ctrl+Alt+L | Format Metal code |

## IDE Compatibility

### Supported IDEs
- ✅ IntelliJ IDEA Community (2024.2+)
- ✅ IntelliJ IDEA Ultimate (2024.2+)
- ✅ CLion (2024.2+)
- ✅ AppCode (2024.2+)
- ✅ WebStorm (2024.2+)
- ✅ PyCharm Professional (2024.2+)

### Version Support
- **Minimum**: 242 (IntelliJ 2024.2)
- **Maximum**: 252.* (IntelliJ 2025.2)
- **Recommended**: Latest stable release

## Example Usage

### 1. Create a Metal Shader

```metal
#include <metal_stdlib>
using namespace metal;

// Type 'kernel' and press Tab
kernel void computeShader(
    texture2d<half, access::read> inTexture [[texture(0)]],
    texture2d<half, access::write> outTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outTexture.get_width() || 
        gid.y >= outTexture.get_height()) {
        return;
    }
    
    half4 color = inTexture.read(gid);
    // Code completion works here: color.
    outTexture.write(color, gid);
}
```

### 2. Use Code Completion

```metal
// Type 'texture.' and see all methods:
texture.read()
texture.write()
texture.sample()
texture.get_width()
texture.get_height()

// Type 'MPS' for Metal Performance Shaders:
MPSImageGaussianBlur
MPSImageHistogram
MPSMatrixMultiplication
```

### 3. Analyze Performance

1. Open your Metal shader
2. Tools → Metal Shader Support → Analyze Shader Complexity
3. Review performance report
4. Follow optimization suggestions

### 4. Transpile to Other Languages (Enterprise)

1. Open `.metal` file
2. Tools → Metal Shader Support → Transpile Shader
3. Choose target: SPIR-V, HLSL, or WGSL
4. Converted file opens automatically

## Troubleshooting

### Plugin Not Loading
```
Solution 1: Check IDE version (must be 2024.2+)
Solution 2: Reinstall plugin
Solution 3: Check IDE logs: Help → Show Log in Finder
```

### No Syntax Highlighting
```
Solution 1: Verify file extension (.metal, .msl, etc.)
Solution 2: Right-click file → Override File Type → Metal
Solution 3: Restart IDE
```

### License Not Activating
```
Solution 1: Check license key format (METAL-XXX-XXXX-XXXX-XXXX)
Solution 2: Verify no extra spaces
Solution 3: Deactivate old license first
Solution 4: Check: Tools → Metal Shader Support → License Information
```

### Features Not Enabled After License
```
Solution 1: License should activate without restart
Solution 2: Check license tier: Tools → License Information
Solution 3: Reopen menus to see enabled items
Solution 4: If still disabled, restart IDE once
```

## Getting Help

### Documentation
- README.md - Project overview
- VERSION_1.0.8_SUMMARY.md - Latest changes
- METAL4_FEATURES.md - Metal 4.0 support
- PRO_FEATURES.md - Pro tier details

### Support
- **Email**: support@jakubjirak.com
- **Issues**: Include plugin version (1.0.8) and IDE version
- **License Problems**: Send license key and error message

### License Purchase
- **Pro**: $49/year - Perfect for individual developers
- **Enterprise**: $199/year - Team features + transpiler
- **Contact**: support@jakubjirak.com

## Next Steps

1. ✅ Install plugin
2. ✅ Test with example Metal file
3. ✅ Activate license (if purchased)
4. 📖 Read FEATURES_OVERVIEW.md
5. 🎯 Check out examples/ directory
6. 💡 Try code templates (type `kernel` + Tab)

## Technical Details

### What's Included
- 50+ Kotlin source files
- 10,000+ lines of code
- 300+ Metal keywords and functions
- 50+ MPS class definitions
- Complete Metal 4.0 support
- Freemium licensing system
- Dynamic plugin loading (no restart!)

### Build Info
- Version: 1.0.8
- Build Date: November 16, 2025
- Package: com.jakubjirak.metalshader
- Size: 1.8 MB
- License: Proprietary

---

**Ready to start?**
Install the plugin and open any `.metal` file to see it in action! 🚀

**Questions?**
Email: support@jakubjirak.com

**Copyright © 2025 Jakub Jirák. All rights reserved.**
