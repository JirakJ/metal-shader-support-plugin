# Metal Shader Support — Screenshot Plan

## Screenshots

### 1. Syntax Highlighting — Compute Kernel
**What to capture:** A `.metal` file open in the editor showing a compute kernel with colorful syntax highlighting. Include Metal-specific keywords (kernel, device, threadgroup), types (float4, texture2d), and attributes ([[texture(0)]], [[buffer(0)]]).

### 2. Code Completion Popup
**What to capture:** The code completion dropdown showing Metal stdlib function suggestions (e.g., sample, read, write, dot, normalize) with parameter hints and inline documentation.

### 3. Live Template Expansion
**What to capture:** A before/after of typing `kernel` + Tab, showing the expanded compute kernel template with placeholder parameters for textures and thread position.

### 4. Real-Time Error Detection
**What to capture:** An editor showing validation errors — a threadgroup size exceeding 1024, an invalid attribute format, and a vector swizzle error. Show inline error highlights and gutter icons.

### 5. Performance Analyzer Results
**What to capture:** The shader complexity analysis output showing detected expensive operations (pow, sqrt, division in loop), thread divergence warnings, and optimization suggestions.

### 6. Live Shader Preview
**What to capture:** The Metal Shader Preview tool window on the right side showing a rendered shader output at 60 FPS alongside the shader source code in the editor.

### 7. GPU Profiler Panel
**What to capture:** The Metal Profiler bottom panel showing GPU cycle estimation, bottleneck detection results, and performance charts for a compute shader.

### 8. Structure View
**What to capture:** The structure view panel showing the outline of a Metal shader file with kernel functions, vertex functions, fragment functions, and struct definitions listed hierarchically.

---

## Video Demo Outline (45 seconds)

| Time | Scene | Description |
|------|-------|-------------|
| 0:00–0:06 | Intro | Show "Metal Shader Support" name. Create a new .metal file in the IDE. |
| 0:06–0:14 | Live template | Type `fragment` + Tab, show the expanded fragment shader template with highlighted placeholders |
| 0:14–0:22 | Code completion | Type a Metal function call, show the completion popup with 300+ keywords and documentation |
| 0:22–0:30 | Error detection | Introduce a threadgroup size error, show the real-time validation warning appear |
| 0:30–0:38 | Live preview | Open the shader preview tool window, show the shader rendering at 60 FPS |
| 0:38–0:45 | Outro | Show pricing tiers (Free, Pro $49/yr, Enterprise $199/yr) and install CTA |
