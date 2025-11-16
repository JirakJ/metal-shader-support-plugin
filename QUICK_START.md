# 🚀 Quick Start Guide - Metal Shader Support v1.0.6

## Installation (2 minutes)

### Step 1: Install Plugin

1. Open **IntelliJ IDEA 2024.2+** (or CLion, AppCode)
2. Press `Cmd + ,` (Settings)
3. Go to **Plugins**
4. Click ⚙️ → **Install Plugin from Disk...**
5. Select: `metal-shader-support-plugin-1.0.6.zip`
6. Click **OK**
7. **Restart IDE**

### Step 2: Activate License (Optional)

**For Pro/Enterprise Features:**

1. Go to **Tools → Metal Shader Support → License → Activate License...**
2. Enter demo key:
   - **Pro**: `METAL-PRO-1234-5678-ABCD`
   - **Enterprise**: `METAL-ENT-9999-8888-WXYZ`
3. Click **Activate**
4. Enjoy all features! 🎉

---

## Quick Test (1 minute)

### Create Your First Metal Shader

1. **Create new file**: Right-click project → New → File → `test.metal`

2. **Type this code:**

```metal
#include <metal_stdlib>
using namespace metal;

kernel void computeShader(
    texture2d<float, access::read> inputTexture [[texture(0)]],
    texture2d<float, access::write> outputTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    float4 color = inputTexture.read(gid);
    color.rgb = pow(color.rgb, 2.2); // Gamma correction
    outputTexture.write(color, gid);
}
```

3. **Watch the magic:**
   - ✨ Syntax highlighting
   - ✨ Auto-completion (try typing `kernel` + Space)
   - ✨ Hover over `kernel` for docs (Ctrl+Q)
   - ✨ Error detection (try typing invalid code)

---

## Features Demo (5 minutes)

### 1. Code Completion

**Type:**
```metal
ker
```
**Press:** `Ctrl + Space`  
**Result:** Completion suggests `kernel`, `vertex`, `fragment`

---

### 2. Live Templates

**Type:** `kernelbasic` + `Tab`  
**Result:** Full kernel template appears!

**Try these snippets:**
- `kernelbasic` → Basic compute kernel
- `vertexbasic` → Basic vertex shader
- `fragmentbasic` → Basic fragment shader
- `ray` → Raytracing setup

---

### 3. GPU Profiler (Pro Feature)

1. Open your `.metal` file
2. Go to **Tools → Metal Shader Support → GPU Profiler (Pro)**
3. See detailed performance metrics:
   - GPU timing
   - Occupancy analysis
   - Memory bandwidth
   - Optimization suggestions

---

### 4. Cross-Platform Transpiler (Enterprise)

1. Open your `.metal` file
2. Go to **Tools → Metal Shader Support → Cross-Platform Transpiler (Enterprise)**
3. Select target:
   - **SPIR-V** → Vulkan
   - **HLSL** → DirectX
   - **WGSL** → WebGPU
4. Click **🔄 Transpile**
5. See converted code side-by-side!

---

### 5. Live Preview (Pro Feature)

1. Go to **View → Tool Windows → Metal Shader Preview**
2. Open a `.metal` file
3. Click **▶ Start Preview**
4. Edit code and see changes in real-time!

---

## Example Shaders

Check out included examples:

```
examples/
├── BasicShaders.metal       (500 lines)
├── AdvancedShaders.metal    (700 lines)
└── Metal4Features.metal     (300 lines)
```

Open these files to see:
- ✅ Compute kernels
- ✅ Vertex/Fragment shaders
- ✅ Texture sampling
- ✅ Raytracing (Metal 4.0)
- ✅ Mesh shaders (Metal 4.0)

---

## Keyboard Shortcuts

| Action | Shortcut (Mac) | Shortcut (Win/Linux) |
|--------|----------------|---------------------|
| Code Completion | `Ctrl + Space` | `Ctrl + Space` |
| Quick Documentation | `Ctrl + Q` | `F1` |
| Parameter Info | `Cmd + P` | `Ctrl + P` |
| Go to Declaration | `Cmd + B` | `Ctrl + B` |
| Find Usages | `Alt + F7` | `Alt + F7` |
| Rename | `Shift + F6` | `Shift + F6` |
| Format Code | `Cmd + Alt + L` | `Ctrl + Alt + L` |

---

## Common Tasks

### Task: Add Compute Kernel

1. Type `kernelbasic` + `Tab`
2. Edit kernel name
3. Add your compute logic
4. Done!

### Task: Profile Performance

1. Open shader file
2. **Tools → GPU Profiler (Pro)**
3. Review metrics
4. Apply suggestions

### Task: Convert to Vulkan

1. Open Metal shader
2. **Tools → Transpiler (Enterprise)**
3. Select **SPIR-V**
4. Click **Transpile**
5. **Save Output**

---

## Tips & Tricks

### 1. Use Auto-Import
Type `MTL` and press `Alt + Enter` → Import Metal stdlib

### 2. Quick Documentation
Hover over any keyword and press `Ctrl + Q` for instant docs

### 3. Performance Hints
Look for ⚠️ yellow warnings for optimization suggestions

### 4. Structure View
Press `Cmd + F12` to see all functions/kernels in file

### 5. File Templates
New File → choose **Metal Shader**, **Metal Kernel**, or **Metal Header**

---

## Troubleshooting

### Plugin Not Working?

**Check IDE Version:**
- Requires IntelliJ IDEA 2024.2+ (build 242+)
- Check: **Help → About**

**Reinstall:**
1. Settings → Plugins
2. Find "Metal Shader Support"
3. Uninstall
4. Restart
5. Install again

### Features Not Appearing?

**Check License:**
- Go to **Tools → Metal Shader Support → License → License Information**
- Activate Pro/Enterprise key if needed

**Restart IDE:**
- Sometimes features need restart to activate

### Syntax Highlighting Not Working?

**File Association:**
1. Settings → Editor → File Types
2. Find "Metal"
3. Add pattern: `*.metal`, `*.msl`, `*.mm`

---

## What's Next?

### Learn More
- Read **METAL4_FEATURES.md** for Metal 4.0 raytracing
- Check **PRO_FEATURES.md** for detailed feature docs
- Explore example shaders in `examples/` folder

### Get Pro License
- Visit: https://metal-shader-plugin.com (coming soon)
- Email: support@jakubjirak.com
- Pricing: $49/year (Pro), $199/year (Enterprise)

### Join Community
- Share your shaders
- Request features
- Report bugs: support@jakubjirak.com

---

## 🎯 5-Minute Challenge

**Can you create a blur shader in 5 minutes?**

1. Create `blur.metal`
2. Type `kernelbasic` + Tab
3. Add this logic:

```metal
kernel void gaussianBlur(
    texture2d<float, access::read> input [[texture(0)]],
    texture2d<float, access::write> output [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    float4 sum = float4(0.0);
    
    // 3x3 Gaussian kernel
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            uint2 offset = uint2(gid.x + x, gid.y + y);
            float4 color = input.read(offset);
            sum += color;
        }
    }
    
    output.write(sum / 9.0, gid);
}
```

4. Watch auto-completion help you!
5. Hover over `texture2d` for docs
6. Run GPU Profiler to see performance
7. Done! 🎉

---

## Support

**Need Help?**
- Email: support@jakubjirak.com
- Subject: [Metal Plugin] Your Question
- Include: IDE version, plugin version, screenshot

**Found a Bug?**
- Email with [Bug] prefix
- Describe steps to reproduce
- Attach log file if possible

---

## License

Copyright (c) 2025 Jakub Jirák. All rights reserved.

**Free Tier**: Basic features, unlimited use  
**Pro Tier**: $49/year, all advanced features  
**Enterprise**: $199/year, transpiler + support  

---

**🚀 Happy Metal Shader Coding!**

Version 1.0.6 | Built with ❤️ by Jakub Jirák
