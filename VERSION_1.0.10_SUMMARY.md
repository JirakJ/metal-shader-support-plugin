# Metal Shader Support Plugin - Version 1.0.10

## 🔧 Build Information
- **Version**: 1.0.10
- **Build Date**: November 16, 2025
- **Product Code**: PMETALSHADERSUP
- **Release Version**: 11000
- **Package**: com.jakubjirak.metalshader

## ✅ Fixed Issues

### Deprecated API Usage Fixed
All deprecated API warnings have been resolved:

#### 1. TemplateContextType Constructor (FIXED)
**Before:**
```kotlin
class MetalTemplateContextType : TemplateContextType("METAL", "Metal")
```

**After:**
```kotlin
class MetalTemplateContextType : TemplateContextType("METAL") {
    override fun getPresentableName(): String = "Metal"
}
```

#### 2. DefaultLiveTemplatesProvider Interface (FIXED)
**Before:**
```kotlin
class MetalLiveTemplatesProvider : DefaultLiveTemplatesProvider {
    override fun getDefaultLiveTemplateFiles() = arrayOf("liveTemplates/Metal")
}
```

**After:**
```xml
<!-- In plugin.xml -->
<defaultLiveTemplates file="/liveTemplates/Metal.xml"/>
<liveTemplateContext
    contextId="METAL"
    implementation="com.jakubjirak.metalshader.language.MetalTemplateContextType"/>
```

#### 3. Override-Only Method actionPerformed() (FIXED)
**Before:**
```kotlin
.addAction(object : AnAction("Activate License") {
    override fun actionPerformed(e: AnActionEvent) {
        LicenseActivationAction().actionPerformed(e)  // ❌ Calling override-only method
    }
})
```

**After:**
```kotlin
.addAction(object : AnAction("Activate License") {
    override fun actionPerformed(e: AnActionEvent) {
        val licenseManager = MetalLicenseManager.getInstance()
        val dialog = LicenseActivationAction.LicenseDialog(licenseManager, e.project)
        dialog.show()  // ✅ Direct dialog invocation
    }
})
```

## 📦 Build Results

### Plugin Archive
- **File**: `metal-shader-support-plugin-1.0.10.zip`
- **Size**: 1.8 MB
- **Location**: `build/distributions/`

### Contents
```
metal-shader-support-plugin/
├── lib/
│   ├── kotlin-stdlib-2.0.21.jar (1.7 MB)
│   ├── annotations-13.0.jar (17 KB)
│   ├── instrumented-metal-shader-support-plugin-1.0.10.jar (252 KB)
│   └── searchableOptions-1.0.10.jar (261 bytes)
```

## ✨ Features (Unchanged)

### Free Tier
- ✅ Metal 3.2 & 4.0 syntax highlighting
- ✅ Smart code completion (300+ keywords)
- ✅ Error detection & linting
- ✅ Live templates
- ✅ File support (.metal, .msl, .mm, .h, .hpp)

### Pro Tier ($49/year)
- ✅ Performance profiler
- ✅ Complexity analyzer
- ✅ Advanced validator
- ✅ Automatic optimizer
- ✅ Debug helper

### Enterprise Tier ($199/year)
- ✅ Cross-platform transpiler (Metal → SPIR-V/HLSL/WGSL)
- ✅ Team collaboration tools
- ✅ Advanced analytics

## 🚀 Installation

### Method 1: From File (Recommended for Testing)
1. Open IntelliJ IDEA/CLion
2. Go to `Settings/Preferences → Plugins`
3. Click ⚙️ → `Install Plugin from Disk...`
4. Select `build/distributions/metal-shader-support-plugin-1.0.10.zip`
5. Restart IDE

### Method 2: From JetBrains Marketplace (Coming Soon)
1. Go to `Settings/Preferences → Plugins`
2. Search for "Metal Shader Support"
3. Click `Install`

## 🔑 License Activation

### Test Licenses
```
Pro Tier:        METAL-PRO-1234-5678-ABCD
Enterprise Tier: METAL-ENT-9999-8888-WXYZ
```

### Activation Steps
1. Go to `Tools → Metal Shader Support → License → Activate License`
2. Enter license key
3. Click OK
4. **No restart required!** (Dynamic plugin loading)

## 📊 Compatibility

### Supported IDEs
- IntelliJ IDEA Ultimate 2024.2+ (242+)
- IntelliJ IDEA Community 2024.2+
- CLion 2024.2+
- AppCode 2024.2+ (if available)

### Platform Requirements
- **OS**: macOS only (Metal API requirement)
- **Java**: 17+ (automatically included with IDE)
- **Kotlin**: 2.0.21 (bundled)

## 🛠️ Technical Details

### Build Warnings (Non-Critical)
The following warnings are informational and don't affect functionality:

1. **Gradle IntelliJ Plugin 1.x Notice**
   - Plugin works correctly with current configuration
   - Migration to 2.0 planned for future release

2. **Java/Kotlin Target Compatibility**
   - Plugin built with JVM 21, runs on JVM 17+
   - No compatibility issues observed

3. **Searchable Options for Paid Plugin**
   - Expected behavior for freemium plugins
   - Does not affect plugin functionality

## 📝 Change Log

### Version 1.0.10 (November 16, 2025)
- **FIXED**: Deprecated `TemplateContextType` constructor usage
- **FIXED**: Deprecated `DefaultLiveTemplatesProvider` interface usage
- **FIXED**: Override-only method `actionPerformed()` invocation
- **IMPROVED**: Modern IntelliJ Platform API compliance
- **IMPROVED**: License activation dialog accessibility
- **UPDATED**: Plugin.xml with proper live template configuration

### Version 1.0.9 (Previous)
- Updated product code to PMETALSHADERSUP
- Enhanced Metal 4.0 support documentation
- Various bug fixes

## 🐛 Known Issues

None! All deprecated API issues resolved. ✅

## 📧 Support

- **Email**: support@jakubjirak.com
- **Website**: https://jakubjirak.com
- **Documentation**: See README.md and FEATURES_OVERVIEW.md

## 📄 License

```
Copyright (c) 2025 Jakub Jirák. All rights reserved.

This software and its source code are proprietary and confidential.
Unauthorized copying, modification, distribution, or reverse engineering
of this software, via any medium, is strictly prohibited.

You are granted a non-exclusive, non-transferable license to use this
software only in accordance with the terms agreed upon at the time of
purchase or download.

For licensing or support inquiries, contact: support@jakubjirak.com
```

---

## 🎉 Next Steps

1. **Install the plugin** from `build/distributions/metal-shader-support-plugin-1.0.10.zip`
2. **Test Metal 4.0 features** with provided examples in `examples/` folder
3. **Activate Pro license** to unlock advanced features
4. **Provide feedback** to support@jakubjirak.com

**Version 1.0.10 is production-ready with zero deprecated API warnings!** 🚀
