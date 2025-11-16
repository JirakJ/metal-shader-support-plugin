# Getting Started with Metal Shader Support Plugin

## 🚀 Quick Start (5 minutes)

### Prerequisites

- Java 21+ installed
- IntelliJ IDEA 2024.1+, CLion 2024.1+, or AppCode 2024.1+
- macOS (recommended for testing with Metal framework)

### Build & Install

1. **Clone and build:**
```bash
git clone https://github.com/yourusername/metal-shader-support-plugin.git
cd metal-shader-support-plugin
./gradlew buildPlugin
```

2. **Install in IDE:**
   - Open IntelliJ IDEA / CLion / AppCode
   - Go to `Settings/Preferences → Plugins`
   - Click ⚙️ (gear icon) → `Install Plugin from Disk...`
   - Select `build/distributions/metal-shader-support-plugin-0.1.0-SNAPSHOT.zip`
   - Restart IDE

3. **Test it:**
   - Create a new file: `test.metal`
   - Start typing and see syntax highlighting!

## 📝 Your First Metal Shader

Create a new `.metal` file and try these templates:

### Vertex Shader (type `vertex` + Tab)
```metal
#include <metal_stdlib>
using namespace metal;

struct VertexIn {
    float4 position [[attribute(0)]];
    float2 texCoord [[attribute(1)]];
};

struct VertexOut {
    float4 position [[position]];
    float2 texCoord;
};

vertex VertexOut vertexShader(
    VertexIn in [[stage_in]],
    constant float4x4& mvpMatrix [[buffer(0)]]
) {
    VertexOut out;
    out.position = mvpMatrix * in.position;
    out.texCoord = in.texCoord;
    return out;
}
```

### Compute Kernel (type `kernel` + Tab)
```metal
kernel void grayscaleKernel(
    texture2d<float, access::read> inTexture [[texture(0)]],
    texture2d<float, access::write> outTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outTexture.get_width() || gid.y >= outTexture.get_height()) {
        return;
    }
    
    float4 color = inTexture.read(gid);
    float luminance = dot(color.rgb, float3(0.299, 0.587, 0.114));
    outTexture.write(float4(luminance), gid);
}
```

## ✨ Features Demo

### 1. Code Completion
Type any of these and press `Ctrl+Space` (or `Cmd+Space` on macOS):
- `tex` → suggests `texture2d`, `texture3d`, etc.
- `floa` → suggests `float`, `float2`, `float3`, `float4`
- `[[` → suggests attributes like `[[texture(0)]]`, `[[buffer(0)]]`
- Built-in functions: `sample()`, `read()`, `write()`, `normalize()`, etc.

### 2. Live Templates
- `vertex` + Tab → Full vertex shader template
- `fragment` + Tab → Full fragment shader template
- `kernel` + Tab → Compute kernel template
- `struct` + Tab → Structure definition

### 3. Error Detection
The plugin automatically detects:
- ✅ Threadgroup size validation (max 1024)
- ✅ Invalid vector swizzle (can't mix `.xyzw` with `.rgba`)
- ✅ Unclosed attribute declarations `[[texture(0)`
- ✅ Missing bounds checking for texture access

### 4. Syntax Highlighting
Semantic colors for:
- **Keywords**: `kernel`, `vertex`, `fragment`, `device`, `threadgroup`
- **Types**: `float4`, `texture2d`, `sampler`
- **Attributes**: `[[texture(0)]]`, `[[stage_in]]`
- **Built-ins**: `sample()`, `normalize()`, `dot()`

## 🔧 Development Workflow

### Run Plugin in Development Mode
```bash
./gradlew runIde
```
This launches a new IDE instance with the plugin installed.

### Run Tests
```bash
./gradlew test
```

### Check Code Quality
```bash
./gradlew check
```

### Build for Distribution
```bash
./gradlew buildPlugin
# Output: build/distributions/metal-shader-support-plugin-*.zip
```

## 📚 Examples

Check the `examples/` directory for:
- `basic_vertex_fragment.metal` - Basic rendering pipeline
- `compute_grayscale.metal` - Image processing kernels (coming soon)
- `advanced_compute.metal` - Threadgroup memory, atomics (coming soon)

## 🎨 Customization

### Color Scheme
1. Go to `Settings → Editor → Color Scheme → Metal`
2. Customize colors for:
   - Keywords
   - Types
   - Attributes
   - Built-in functions
   - Comments

### Live Templates
Add your own templates:
1. Go to `Settings → Editor → Live Templates`
2. Select `Metal` group
3. Click `+` to add new template

## 🐛 Troubleshooting

### Plugin doesn't load
- Check IDE version (must be 2024.1+)
- Check Java version: `java -version` (must be 21+)
- Check logs: `Help → Show Log in Finder/Explorer`

### Syntax highlighting not working
- Ensure file extension is `.metal` or `.msl`
- Try `File → Invalidate Caches → Invalidate and Restart`

### Code completion not appearing
- Press `Ctrl+Space` / `Cmd+Space` to trigger
- Check `Settings → Editor → General → Code Completion` is enabled

## 📖 Next Steps

### Phase 2 Features (Coming Soon)
- 🎬 **Live Preview** - See shader output in real-time
- 🐞 **Debugger** - Step through shader execution
- 📊 **Profiler** - GPU timing and optimization hints

### Contributing
Want to add features? See [CONTRIBUTING.md](CONTRIBUTING.md)

### Get Help
- 🐛 Issues: [GitHub Issues](https://github.com/yourusername/metal-shader-support-plugin/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/yourusername/metal-shader-support-plugin/discussions)
- 📧 Email: support@metalshader.dev

## 🎯 Pro Tips

1. **Keyboard Shortcuts**
   - `Cmd/Ctrl + /` - Toggle line comment
   - `Cmd/Ctrl + Shift + /` - Toggle block comment
   - `Cmd/Ctrl + D` - Duplicate line
   - `Alt + Enter` - Show quick fixes

2. **Code Navigation**
   - `Cmd/Ctrl + B` - Go to definition
   - `Cmd/Ctrl + F12` - File structure
   - `Shift + Shift` - Search everywhere

3. **Productivity**
   - Use Live Templates for common patterns
   - Enable auto-import for Metal stdlib
   - Set up file templates for new shaders

## 📦 Example Project Structure

```
MyMetalProject/
├── shaders/
│   ├── vertex/
│   │   └── basic.metal
│   ├── fragment/
│   │   └── lighting.metal
│   └── compute/
│       ├── blur.metal
│       └── grayscale.metal
├── include/
│   └── common.h
└── resources/
    └── textures/
```

Happy Metal Shader coding! 🚀✨
