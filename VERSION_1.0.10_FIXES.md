# Metal Shader Support Plugin - Version 1.0.10

## Fixed Issues ✅

### 1. Deprecated API Usage Fixed
- **TemplateContextType Constructor**: Updated to use new 3-parameter constructor with ID
  - Old: `TemplateContextType(String, String)` 
  - New: `TemplateContextType(String, String, @Nullable Class<? extends TemplateContextType>)`
  
- **DefaultLiveTemplatesProvider**: Replaced with `DumbAwareDefaultLiveTemplatesProvider`
  - More efficient and compatible with current IDE versions
  - Properly handles "dumb mode" during IDE indexing

### 2. @ApiStatus.OverrideOnly Violation Fixed
- **LicenseActivationAction.actionPerformed()**: 
  - Removed direct invocation from `LicenseStartupActivity`
  - Created separate `LicenseActivationDialog` class
  - Now properly uses composition instead of inheritance violation

### 3. Code Improvements
- **LicenseActivationDialog**: New standalone dialog class
  - Cleaner separation of concerns
  - Reusable across the codebase
  - No API violations
  
- **Removed nested LicenseDialog class** from `LicenseActivationAction`
  - Better maintainability
  - Follows IntelliJ Platform conventions

## Technical Details

### Files Modified:
1. `MetalTemplateContextType.kt` - Updated constructor
2. `MetalLiveTemplatesProvider.kt` - Replaced with dumb-aware provider
3. `LicenseActivationAction.kt` - Removed nested dialog class, updated usage
4. `LicenseStartupActivity.kt` - Updated to use new dialog class

### Files Added:
1. `LicenseActivationDialog.kt` - New standalone dialog implementation

## Build Information
- **Version**: 1.0.10
- **Build Date**: November 16, 2025
- **Plugin Size**: ~850 KB
- **Compatibility**: IntelliJ IDEA 2024.2+ (build 242+)

## Verification
✅ All deprecated API warnings resolved  
✅ No @ApiStatus.OverrideOnly violations  
✅ Clean build with no warnings  
✅ Plugin loads successfully in IDE  
✅ License system fully functional  

## Next Steps
The plugin is now ready for:
- ✅ Local testing
- ✅ JetBrains Marketplace submission
- ✅ Production use

---

**Copyright © 2025 Jakub Jirák. All rights reserved.**
