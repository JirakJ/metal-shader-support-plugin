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

### 4. Transpile to Other Languages

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

## Getting Help

### Documentation
- README.md - Project overview
- CHANGELOG.md - Latest changes
- METAL4_FEATURES.md - Metal 4.0 support

### Support
- **Email**: support@jakubjirak.com
- **Issues**: Include plugin version (1.0.8) and IDE version

## Next Steps

1. ✅ Install plugin
2. ✅ Test with example Metal file
3. 📖 Read FEATURES_OVERVIEW.md
5. 🎯 Check out examples/ directory
6. 💡 Try code templates (type `kernel` + Tab)

## Technical Details

### What's Included
- 50+ Kotlin source files
- 10,000+ lines of code
- 300+ Metal keywords and functions
- 50+ MPS class definitions
- Complete Metal 4.0 support
- Dynamic plugin loading (no restart!)

### Build Info
- Version: 1.0.8
- Build Date: November 16, 2025
- Package: com.jakubjirak.metalshader
- Size: 1.8 MB
- License: Apache-2.0

---

**Ready to start?**
Install the plugin and open any `.metal` file to see it in action! 🚀

**Questions?**
Email: support@jakubjirak.com

