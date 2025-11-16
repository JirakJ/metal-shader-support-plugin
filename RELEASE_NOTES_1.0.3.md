# Metal Shader Support Plugin - Release Notes v1.0.3

## 🎯 Major Improvements: License Management & Persistence

### New Features

#### 1. **License Submenu in Tools Menu**
   - All license actions now organized under **Tools → Metal Shader Support → License**
   - Clean, professional menu structure
   - Better discoverability of license features

#### 2. **Persistent License Storage** ✨
   - License is now **permanently saved** in IDE preferences
   - Survives IDE restarts and updates
   - Stored in `~/.config/JetBrains/<IDE>/options/metalShaderLicense.xml`
   - No need to re-enter license key after restart

#### 3. **License Information Dialog**
   - New action: **Tools → Metal Shader Support → License → License Information**
   - Shows current tier (FREE/PRO/ENTERPRISE)
   - Displays activation date
   - Masked license key display (security)
   - Feature comparison for each tier
   - Upgrade information for Free users

#### 4. **Deactivate License Action**
   - New action: **Tools → Metal Shader Support → License → Deactivate License**
   - Safely deactivate license (e.g., to move to another machine)
   - Confirmation dialog to prevent accidental deactivation
   - Disabled when no license is active (better UX)

#### 5. **Startup Notifications** 🔔
   - Smart notifications on IDE startup
   - **Free Tier Users**: Friendly reminder about Pro features with "Activate License" button
   - **Pro/Enterprise Users**: Confirmation that license is active
   - Non-intrusive balloon notifications
   - Action buttons for quick access

### Improvements

#### License Activation Dialog
   - Enhanced UI with emoji icons for better readability
   - Shows current license tier and status
   - Complete pricing information ($49 Pro, $199 Enterprise)
   - Detailed feature list for each tier
   - Better error messages with format examples
   - Restart reminder after activation

#### License Manager
   - Proper state persistence using IntelliJ's `PersistentStateComponent`
   - Thread-safe license storage
   - Better validation messages
   - Tier information always accessible

### Technical Details

#### Files Added/Modified
- ✅ `LicenseInfoAction.kt` - New action for license information
- ✅ `DeactivateLicenseAction.kt` - New action to deactivate license
- ✅ `LicenseStartupActivity.kt` - Startup notifications
- ✅ `LicenseActivationAction.kt` - Enhanced UI and messaging
- ✅ `plugin.xml` - License submenu structure, notification group, startup activity
- ✅ `gradle.properties` - Version bump to 1.0.3

#### License Persistence
```kotlin
@State(
    name = "MetalShaderLicense",
    storages = [Storage("metalShaderLicense.xml")]
)
class MetalLicenseManager : PersistentStateComponent<State>
```

License data is automatically:
- ✅ Saved on activation
- ✅ Loaded on IDE startup
- ✅ Preserved across updates
- ✅ Synchronized across IDE instances

### Menu Structure

```
Tools
  └─ Metal Shader Support
      ├─ License
      │   ├─ Activate License...
      │   ├─ License Information
      │   └─ Deactivate License
      ├─ (separator)
      ├─ Analyze Shader Complexity (Pro)
      ├─ Profile Shader Performance (Pro)
      ├─ Validate Shader (Pro)
      └─ Optimize Shader (Pro)
```

### Testing License Keys

For testing purposes:

- **Pro License**: `METAL-PRO-1234-5678-ABCD`
- **Enterprise License**: `METAL-ENT-1234-5678-ABCD`

Format validation:
- Pro licenses must start with `METAL-PRO-`
- Enterprise licenses must start with `METAL-ENT-`

### Build Information

- **Version**: 1.0.3
- **Build Date**: November 16, 2025
- **Minimum IDE Version**: 242 (2024.2+)
- **Plugin Size**: ~250 KB
- **Location**: `build/distributions/Metal-Shader-Support-1.0.3.zip`

### Installation

1. Download `Metal-Shader-Support-1.0.3.zip` from `build/distributions/`
2. In IntelliJ IDEA: **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select the ZIP file
4. Restart IDE
5. Go to **Tools → Metal Shader Support → License → Activate License** to activate Pro features

### Known Issues

None. All features working as expected.

### Migration from 1.0.2

No migration needed. If you activated a license in 1.0.2, it will be saved automatically in 1.0.3 and persist across restarts.

### Next Steps

Future versions will include:
- Online license validation
- License expiration tracking
- Automatic renewal reminders
- Multi-machine license management
- Team license dashboard

---

## Support

- **Email**: support@jakubjirak.com
- **Website**: https://jakubjirak.com
- **License Purchase**: https://jakubjirak.com

---

© 2025 Jakub Jirák. All rights reserved. Proprietary software.
