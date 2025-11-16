# Metal Shader Support Plugin - Installation Guide v1.0.8

## 📦 Quick Install

### Step 1: Get the Plugin
Plugin file: `build/distributions/metal-shader-support-plugin-1.0.8.zip` (1.8 MB)

### Step 2: Install in IntelliJ IDEA
1. Open **IntelliJ IDEA** (2024.2 or newer)
2. Go to **Settings/Preferences** (`Cmd+,` on Mac, `Ctrl+Alt+S` on Windows/Linux)
3. Navigate to **Plugins**
4. Click the gear icon ⚙️
5. Select **Install Plugin from Disk...**
6. Choose `metal-shader-support-plugin-1.0.8.zip`
7. Click **OK**
8. Restart IDE (only required first time)

---

## 🔑 Activate Your License (Optional)

### FREE Tier (Default)
No license needed! Just install and start using basic features.

### PRO Tier ($49/year)
1. Go to **Tools → Metal Shader Support → License → Activate License**
2. Enter your Pro license key: `METAL-PRO-XXXX-XXXX-XXXX`
3. Click **Activate**
4. Pro features enabled immediately (no restart!)

### ENTERPRISE Tier ($199/year)
1. Go to **Tools → Metal Shader Support → License → Activate License**
2. Enter your Enterprise license key: `METAL-ENT-XXXX-XXXX-XXXX`
3. Click **Activate**
4. All features unlocked instantly!

---

## 🧪 Test Installation

### 1. Open a Metal File
Create a new file `test.metal`:

```metal
#include <metal_stdlib>
using namespace metal;

kernel void testKernel(
    texture2d<float, access::read> inTexture [[texture(0)]],
    texture2d<float, access::write> outTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    float4 color = inTexture.read(gid);
    outTexture.write(color, gid);
}
```

### 2. Verify Syntax Highlighting
- Keywords should be colored (kernel, texture2d, float4)
- Attributes should be highlighted [[texture(0)]]
- Comments should be distinct

### 3. Test Code Completion
Type `metal::` and press `Ctrl+Space` - you should see:
- abs, acos, asin, atan, ceil, clamp, cos, cross, distance...
- 300+ Metal stdlib functions

### 4. Use Live Templates
Type `kernel` + `Tab` - expands to full compute kernel template

---

## 🎯 Pro Features Demo

### Test Live Preview (PRO Required)
1. Open `examples/basic_shader.metal`
2. Go to **View → Tool Windows → Metal Shader Preview**
3. Click **Start Preview**
4. See your shader rendered in real-time!

### Test GPU Profiler (PRO Required)
1. Open any Metal shader file
2. Go to **View → Tool Windows → Metal Profiler**
3. Click **Profile Shader**
4. See GPU timing and performance metrics!

### Test Debugger (PRO Required)
1. Open any Metal shader file
2. Go to **View → Tool Windows → Metal Debugger**
3. Click **Start Debug Session**
4. Set breakpoints and inspect variables!

---

## 🏢 Enterprise Features Demo

### Test Transpiler (ENTERPRISE Required)
1. Open any Metal shader file
2. Go to **Tools → Metal Shader Support → Enterprise → Transpile to SPIR-V**
3. Select output format (SPIR-V, HLSL, or WGSL)
4. Click **Transpile**
5. Cross-platform shader code generated!

---

## 🔍 Verify License Status

### Check Your Current Tier
1. Go to **Tools → Metal Shader Support → License → License Information**
2. You'll see:
   - Current tier (FREE, PRO, or ENTERPRISE)
   - License key (if activated)
   - Activation date
   - Available features

---

## 🎁 Trial Licenses

### Pro Trial (14 days)
Contact: support@jakubjirak.com

### Enterprise Trial (7 days)
Contact: support@jakubjirak.com

### Demo License (Full Access)
Use this enterprise license for testing:
```
METAL-ENT-9999-8888-WXYZ
```

---

## 🛠️ Troubleshooting

### Plugin Not Loading
**Problem:** Plugin installed but features not working  
**Solution:** 
1. Check IDE version: Must be 2024.2+ (build 242+)
2. Restart IDE
3. Check **Settings → Plugins** - Metal Shader Support should be enabled

### Syntax Highlighting Not Working
**Problem:** Metal files show plain text  
**Solution:**
1. Right-click file → **Associate with File Type → Metal**
2. Or rename file with `.metal` extension

### Pro Features Disabled
**Problem:** License activated but Pro features locked  
**Solution:**
1. Check license: **Tools → Metal Shader Support → License → License Information**
2. Verify license key starts with `METAL-PRO-` or `METAL-ENT-`
3. Deactivate and reactivate license

### Tool Windows Not Showing
**Problem:** Preview/Debugger/Profiler windows missing  
**Solution:**
1. Go to **View → Tool Windows**
2. Look for "Metal Shader Preview", "Metal Debugger", "Metal Profiler"
3. If missing, activate Pro/Enterprise license

---

## 💡 Quick Tips

### Keyboard Shortcuts
- `Ctrl+Space` - Code completion
- `Ctrl+Q` (Cmd+J on Mac) - Quick documentation
- `Cmd+B` / `Ctrl+B` - Go to definition
- `Alt+F7` - Find usages
- `Shift+F6` - Rename

### Live Templates (Tab to expand)
- `kernel` → Compute kernel
- `vertex` → Vertex shader
- `fragment` → Fragment shader
- `texture` → Texture sampling
- `raytracing` → Metal 4 raytracing
- `meshlet` → Metal 4 mesh shader

### Example Projects
Check `examples/` directory:
- `basic_shader.metal` - Simple texture processing
- `compute_blur.metal` - Gaussian blur filter
- `raytracing_example.metal` - Metal 4 path tracer
- `mesh_shader_example.metal` - Metal 4 mesh shading

---

## 📚 Documentation

### Official Docs (Coming Soon)
- User Guide: https://jakubjirak.com/metal-plugin/docs
- API Reference: https://jakubjirak.com/metal-plugin/api
- Tutorial Videos: https://jakubjirak.com/metal-plugin/tutorials

### Metal Resources
- Apple Metal Documentation: https://developer.apple.com/metal/
- Metal Shading Language Specification: https://developer.apple.com/metal/Metal-Shading-Language-Specification.pdf
- Metal Best Practices: https://developer.apple.com/documentation/metal/best_practices

---

## 📞 Support

### Need Help?
- **Email:** support@jakubjirak.com
- **Issues:** Report bugs or request features

### License Purchase
- **Pro License:** $49/year - support@jakubjirak.com
- **Enterprise License:** $199/year - support@jakubjirak.com
- **Site License:** $2,999/year (50 seats) - support@jakubjirak.com

---

## 🎉 Welcome to Metal Shader Development!

You're all set! Start building amazing GPU-powered applications with Metal.

**Happy Coding! 🚀**

---

_© 2025 Jakub Jirák. All rights reserved._
