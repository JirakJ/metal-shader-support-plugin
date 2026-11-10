# Metal Shader Support Plugin

[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE) ![Version](https://img.shields.io/badge/version-2026.8.0-green)

> **Free & open source.** Since 2026.8.0 the plugin is completely free — every feature is unlocked for everyone, no license needed — and the source code is available under the Apache License 2.0.

[![Platform](https://img.shields.io/badge/platform-IntelliJ-orange)](https://www.jetbrains.com/idea/)
[![Metal](https://img.shields.io/badge/Metal-4.0-red)](https://developer.apple.com/metal/)

**Professional Metal Shading Language support for JetBrains IDEs.** Build high-performance GPU applications with full Metal 3.2 and Metal 4.0 support for iOS, macOS, and visionOS.

## Features

### ✨ Core Features

- **Syntax Highlighting** - Full Metal Shading Language 3.2 support
  - Keywords: `kernel`, `vertex`, `fragment`, `device`, `threadgroup`
  - Types: `float4`, `texture2d`, `sampler`, matrices, vectors
  - Attributes: `[[texture(0)]]`, `[[buffer(0)]]`, `[[stage_in]]`
  - Built-in functions: `sample()`, `read()`, `write()`, math functions

- **Code Completion** - IntelliSense for Metal
  - Auto-complete Metal stdlib functions
  - Attribute suggestions with descriptions
  - Template snippets (vertex/fragment/compute shaders)

- **Error Detection** - Real-time linting
  - Threadgroup size validation (max 1024 threads)
  - Texture access bounds checking
  - Attribute format validation
  - Vector swizzle validation

- **Editor Features**
  - Comment/uncomment (// and /* */)
  - Brace matching
  - Live templates for common patterns

## Installation

### From JetBrains Marketplace (Coming Soon)
1. Open Settings → Plugins
2. Search for "Metal Shader Support"
3. Click Install

### Manual Installation
1. Download the latest release from [Releases](https://github.com/yourusername/metal-shader-support-plugin/releases)
2. Open Settings → Plugins → ⚙️ → Install Plugin from Disk
3. Select the downloaded `.zip` file
4. Restart IDE

## Quick Start

1. Create a new file with `.metal` or `.msl` extension
2. Start typing Metal shader code
3. Use code completion with `Ctrl+Space` (Cmd+Space on macOS)
4. Try live templates: type `vertex` + Tab, `fragment` + Tab, or `kernel` + Tab

### Example

```metal
// Type "kernel" + Tab to generate this template
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

## Supported File Types

- `.metal` - Metal shader files
- `.msl` - Metal Shading Language files

## Requirements

- IntelliJ IDEA 2024.1+ / CLion 2024.1+ / AppCode 2024.1+
- Java 21 or higher
- macOS recommended (Metal is Apple-only framework)

## Roadmap

- [ ] Enhanced live shader preview
- [ ] Advanced GPU debugger integration
- [ ] Team collaboration features
- [ ] CI/CD integration
- [ ] Advanced analytics

## Development

### Building from Source

```bash
git clone https://github.com/yourusername/metal-shader-support-plugin.git
cd metal-shader-support-plugin
./gradlew buildPlugin
```

The plugin will be built to `build/distributions/metal-shader-support-plugin-*.zip`

### Running in Development

```bash
./gradlew runIde
```

### Testing

```bash
./gradlew test
```

## Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/metal-shader-support-plugin/issues)
- **Email**: support@metalshader.dev
- **Documentation**: [Wiki](https://github.com/yourusername/metal-shader-support-plugin/wiki)

## Acknowledgments

- Built with [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- Metal Shading Language specification by Apple
- Inspired by shader tooling in Visual Studio and RenderDoc

---

**Made with ❤️ for the Metal developer community**

*Perfect for iOS/macOS game development, GPU compute, and video processing*

## Contributing

Issues and pull requests are welcome at https://github.com/JirakJ/metal-shader-support-plugin.

## License

Copyright 2026 Jakub Jirák. Licensed under the [Apache License, Version 2.0](LICENSE).
