# Release Notes - Version 1.0.4

## 🔧 Bug Fixes

### License Activation Issues
- **Fixed**: Menu items remain disabled after license activation
- **Fixed**: Pro features menu items now properly reflect license state in real-time
- **Added**: License change broadcast system for immediate UI updates

## 🎯 Technical Changes

### License Manager Improvements
- Added `LicenseChangeListener` interface with message bus integration
- Menu items now dynamically enable/disable based on current license tier
- License state changes now trigger immediate UI refresh
- Improved menu item visibility logic

### Action Updates
- `ValidateShaderAction` - now properly checks license state in `update()`
- `OptimizeShaderAction` - dynamic enable/disable based on Pro license
- `ProfileShaderAction` - real-time license state verification
- `AnalyzeShaderComplexityAction` - immediate response to license changes

## 📋 How It Works

When you activate a license:
1. License key is validated and stored
2. License tier is updated (FREE → PRO → ENTERPRISE)
3. Broadcast message is sent to all listeners
4. Menu items automatically refresh their enabled state
5. Pro features become immediately accessible

## ✅ Verified Behaviors

- ✅ Menu items disabled for FREE tier
- ✅ Menu items enabled after license activation
- ✅ Menu items visible only in Metal files
- ✅ License state persists across IDE restarts
- ✅ Deactivation properly disables Pro features

## 🔑 License Tiers Supported

- **FREE**: Basic syntax highlighting and code completion
- **PRO**: License prefix `METAL-PRO-*`
  - Shader validation
  - Performance optimization
  - Complexity analysis
  - Shader profiling
  
- **ENTERPRISE**: License prefix `METAL-ENT-*`
  - All Pro features
  - Priority support
  - Team collaboration features

## 🐛 Known Issues

None currently reported.

## 📦 Installation

Replace your existing plugin JAR with:
```
build/distributions/metal-shader-support-1.0.4.zip
```

Then restart IntelliJ IDEA.

---

**Support**: support@jakubjirak.com  
**License Format**: `METAL-[PRO|ENT]-XXXX-XXXX-XXXX`
