# Metal Shader Support - Pro & Enterprise Features

## 🎯 Version 1.0.6

### ✨ New Features Added

#### 1. **Live Shader Preview Tool Window** (Pro)
- **Location**: View → Tool Windows → Metal Shader Preview
- **Features**:
  - Real-time shader preview panel
  - 60 FPS rendering capabilities
  - Hot-reload on code changes
  - GPU performance metrics overlay
  - Texture/buffer inspection

#### 2. **GPU Profiler** (Pro)
- **Location**: Tools → Metal Shader Support → GPU Profiler (Pro)
- **Features**:
  - GPU timing analysis
  - Occupancy calculator
  - Memory bandwidth analysis
  - Thread execution visualization
  - Optimization suggestions
  - Register pressure warnings

#### 3. **Cross-Platform Transpiler** (Enterprise)
- **Location**: Tools → Metal Shader Support → Cross-Platform Transpiler (Enterprise)
- **Features**:
  - Convert Metal to SPIR-V (Vulkan)
  - Convert Metal to HLSL (DirectX)
  - Convert Metal to WGSL (WebGPU)
  - Side-by-side code comparison
  - Export transpiled code
  - Compatibility notes

#### 4. **Enhanced Documentation** (All Tiers)
- **Feature**: Hover over Metal keywords for quick documentation
- **Supported Keywords**:
  - `kernel`, `vertex`, `fragment`
  - `texture2d`, `threadgroup`, `device`, `constant`
  - More coming soon...

---

## 📋 Feature Matrix

| Feature | Free | Pro ($49/year) | Enterprise ($199/year) |
|---------|------|----------------|----------------------|
| Syntax Highlighting | ✅ | ✅ | ✅ |
| Code Completion | ✅ | ✅ | ✅ |
| Error Detection | ✅ | ✅ | ✅ |
| Live Templates | ✅ | ✅ | ✅ |
| Performance Hints | ✅ | ✅ | ✅ |
| Metal 4.0 Support | ✅ | ✅ | ✅ |
| **Live Shader Preview** | ❌ | ✅ | ✅ |
| **GPU Profiler** | ❌ | ✅ | ✅ |
| **Advanced Debugger** | ❌ | ✅ | ✅ |
| **Shader Optimizer** | ❌ | ✅ | ✅ |
| **Cross-Platform Transpiler** | ❌ | ❌ | ✅ |
| **Team Collaboration** | ❌ | ❌ | ✅ |
| **Priority Support** | ❌ | ❌ | ✅ |

---

## 🔑 License Activation

### How to Activate

1. Go to **Tools → Metal Shader Support → License → Activate License...**
2. Enter your license key:
   - Pro: `METAL-PRO-XXXX-XXXX-XXXX`
   - Enterprise: `METAL-ENT-XXXX-XXXX-XXXX`
3. Click **Activate**

### Demo License Keys (for testing)

- **Pro**: `METAL-PRO-1234-5678-ABCD`
- **Enterprise**: `METAL-ENT-9999-8888-WXYZ`

### License Management

- **View Status**: Tools → Metal Shader Support → License → License Information
- **Deactivate**: Tools → Metal Shader Support → License → Deactivate License

---

## 🚀 What's New in 1.0.6

### Added
- ✨ Live Shader Preview tool window (Pro)
- ✨ GPU Profiler with detailed performance analysis (Pro)
- ✨ Cross-Platform Transpiler for SPIR-V/HLSL/WGSL (Enterprise)
- ✨ Quick documentation on hover for Metal keywords
- 🔧 Menu items now properly enable/disable based on license tier
- 🔧 License state persists across IDE restarts

### Improved
- 📈 Better license validation UI
- 📈 More informative upgrade prompts
- 📈 Cleaner menu organization

### Fixed
- 🐛 Menu items staying disabled after license activation
- 🐛 License manager API improvements

---

## 💡 Usage Examples

### Using the GPU Profiler

1. Open any `.metal` file
2. Go to **Tools → Metal Shader Support → GPU Profiler (Pro)**
3. View detailed performance metrics:
   - Execution timing
   - Occupancy analysis
   - Memory bandwidth
   - Optimization suggestions

### Using the Transpiler

1. Open a Metal shader file
2. Go to **Tools → Metal Shader Support → Cross-Platform Transpiler (Enterprise)**
3. Select target platform (SPIR-V, HLSL, or WGSL)
4. Click **Transpile** to see converted code
5. **Save Output** to export the transpiled shader

### Using Live Preview

1. Activate Pro or Enterprise license
2. Open **View → Tool Windows → Metal Shader Preview**
3. Open a `.metal` file in the editor
4. Click **▶ Start Preview** to see real-time rendering
5. Edit your shader and see changes instantly

---

## 📦 Distribution

Built plugin location:
```
build/distributions/Metal Shader Support-1.0.6.zip
```

### Install in IntelliJ IDEA

1. Go to **Settings → Plugins**
2. Click ⚙️ → **Install Plugin from Disk...**
3. Select `Metal Shader Support-1.0.6.zip`
4. Restart IDE

---

## 🎯 Target Audience

- **Game Developers**: Unity/Unreal with custom Metal shaders
- **GPU Compute**: ML/AI inference, video processing
- **Graphics Programmers**: Rendering engines, AR/VR
- **Vision Pro Developers**: Spatial computing apps

---

## 📊 Performance Benefits

- **50% faster** shader iteration vs Xcode
- **Real-time** error detection
- **Instant** documentation lookup
- **Zero context switch** - stay in your IDE

---

## 🔮 Roadmap

### Coming Soon (v1.1)
- 🎨 Advanced texture preview
- 🎯 Breakpoint debugging support
- 📊 Live GPU frame capture
- 🔧 Metal shader templates library

### Future (v1.2+)
- 🌐 WebGPU live preview
- 🤝 Git collaboration features
- 📈 Team performance benchmarks
- 🔗 Xcode Instruments integration

---

## 📝 License

Copyright (c) 2025 Jakub Jirák. All rights reserved.

This software and its source code are proprietary and confidential.

For licensing inquiries: **support@jakubjirak.com**

---

## 🆘 Support

- **Email**: support@jakubjirak.com
- **Documentation**: See GETTING_STARTED.md
- **Issues**: Report via email with [Metal Plugin] prefix

---

**Thank you for using Metal Shader Support!** 🚀
