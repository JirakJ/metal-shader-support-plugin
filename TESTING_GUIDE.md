# Metal Shader Support Plugin - Testing Guide

## Quick Test Checklist

### 1. Installation & First Launch ✅

```bash
# Build plugin
./gradlew clean buildPlugin

# Install manually
# Settings → Plugins → ⚙️ → Install Plugin from Disk
# Select: build/distributions/Metal-Shader-Support-1.0.3.zip
```

**Expected**:
- ✅ Plugin installs without errors
- ✅ IDE restarts successfully
- ✅ Balloon notification appears: "Metal Shader Support - Free Tier"
- ✅ Notification has "Activate License" button

### 2. License Menu Structure ✅

**Test**: Navigate to **Tools → Metal Shader Support**

**Expected Menu**:
```
Tools
  └─ Metal Shader Support (with submenu arrow)
      ├─ License >
      │   ├─ Activate License...
      │   ├─ License Information
      │   └─ Deactivate License (disabled initially)
      ├─ (separator)
      ├─ Analyze Shader Complexity (Pro)
      ├─ Profile Shader Performance (Pro)
      ├─ Validate Shader (Pro)
      └─ Optimize Shader (Pro)
```

### 3. License Information (Free Tier) ✅

**Test**: Click **License → License Information**

**Expected Dialog**:
- Title: "Metal Shader Support - License Information"
- Shows: "License Tier: FREE"
- Shows: "Status: Free Tier - Basic features"
- Shows: "Activation Date: Not activated"
- Lists Free tier features (4 items)
- Lists Pro tier features with "Upgrade to Pro" (6 items)
- Shows pricing: $49/year Pro, $199/year Enterprise
- Contact info: support@jakubjirak.com

### 4. Activate Pro License ✅

**Test**: Click **License → Activate License...**

**Step 1 - Dialog Opens**:
- Title: "Metal Shader Support - License Activation"
- Shows current tier: "FREE - Free Tier - Basic features"
- Shows Pro features with emojis (✨🎯🔍⚡🐛📧)
- Shows pricing table
- Empty text field for license key
- "OK" and "Cancel" buttons

**Step 2 - Enter Invalid Key**:
```
Enter: INVALID-KEY-123
```
Expected:
- ❌ Error dialog: "Invalid license key"
- Shows valid formats (METAL-PRO-*, METAL-ENT-*)
- Shows contact info

**Step 3 - Enter Valid Pro Key**:
```
Enter: METAL-PRO-1234-5678-ABCD
```
Expected:
- ✅ Success dialog: "License activated successfully!"
- Shows: "Tier: PRO"
- Shows: "Status: Pro Tier - All features unlocked"
- Message: "All Pro features are now unlocked"
- Message: "Restart IDE to fully apply changes"

### 5. Verify License Persistence ✅

**Test**: Restart IDE after activating license

**Expected**:
- ✅ After restart, go to **License → License Information**
- Shows: "License Tier: PRO"
- Shows: "Status: Pro Tier - All features unlocked"
- Shows: "Activation Date: 2025-11-16 10:53:00"
- Shows: "License Key: METAL-PRO-12...ABCD" (masked)
- Lists all Pro features with checkmarks ✅
- Thank you message displayed

**Expected Notification**:
- Balloon: "Metal Shader Support - PRO License Active"
- Message: "All Pro features are unlocked. Thank you for your support!"

### 6. License Information (Pro Tier) ✅

**Test**: Check **License → License Information** after activation

**Expected**:
- All sections populated correctly
- Activation date shows current date/time
- License key is properly masked (first 12 + "..." + last 4 chars)
- Pro features list shows ✅ checkmarks
- No upgrade message (already Pro)

### 7. Deactivate License ✅

**Test**: Click **License → Deactivate License**

**Expected**:
- Menu item is now **enabled** (was disabled in Free tier)
- Confirmation dialog appears:
  - Title: "Metal Shader Support - Deactivate License"
  - Shows current tier: "PRO"
  - Warning: "This will revert you to the Free tier"
  - Two buttons: "Deactivate" and "Cancel"

**Test**: Click "Deactivate"

**Expected**:
- ✅ Success dialog: "License deactivated successfully"
- Message: "You are now using the Free tier"
- Instructions to reactivate

**Test**: Verify deactivation
- Go to **License → License Information**
- Shows: "License Tier: FREE"
- Shows: "Activation Date: Not activated"
- Upgrade message appears again

### 8. Verify Persistence After Deactivation ✅

**Test**: Restart IDE after deactivation

**Expected**:
- License remains in FREE tier
- Free tier notification appears
- No license key in storage

### 9. Activate Enterprise License ✅

**Test**: Activate with enterprise key

```
Enter: METAL-ENT-9999-8888-WXYZ
```

**Expected**:
- ✅ Success dialog with "ENTERPRISE" tier
- License Information shows:
  - "License Tier: ENTERPRISE"
  - "Status: Enterprise Tier - All features + priority support"
  - Lists Enterprise features (5 items with ✅)
  - Thank you message for enterprise support

### 10. Pro Actions Availability ✅

**Test**: Verify Pro actions are available/unavailable based on license

**Free Tier**:
- Open any `.metal` file
- Right-click → context menu
- "Analyze Metal Shader Complexity" appears (Pro features show dialog)

**Pro Tier**:
- Same action now executes (no restriction dialog)

### 11. File Type Recognition ✅

**Test**: Create test files with different extensions

```bash
# Create test files
touch test.metal      # Metal shader
touch test.msl        # Metal Shading Language
touch test.mm         # Objective-C++ with Metal
touch shader.h        # Header with Metal
touch compute.hpp     # C++ header with Metal
```

**Expected**:
- All files recognized as "Metal" file type
- Metal icon appears in project tree
- Syntax highlighting works
- Code completion works

### 12. Startup Notification Preferences ✅

**Test**: Check notification behavior on multiple startups

**Expected**:
- Free tier: Notification appears each time (can be dismissed)
- Pro tier: Notification appears once per session
- Notifications don't block workflow
- "Don't show again" preference works

### 13. License Storage Location ✅

**Test**: Find the license configuration file

```bash
# macOS location
cat ~/Library/Application\ Support/JetBrains/IntelliJIdea2024.2/options/metalShaderLicense.xml

# Linux location
cat ~/.config/JetBrains/IntelliJIdea2024.2/options/metalShaderLicense.xml
```

**Expected XML**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<application>
  <component name="MetalShaderLicense">
    <option name="licenseKey" value="METAL-PRO-1234-5678-ABCD" />
    <option name="activationDate" value="1700000000000" />
    <option name="tier" value="PRO" />
  </component>
</application>
```

### 14. Error Cases ✅

**Test**: Edge cases and error handling

1. **Empty license key**:
   - Leave field blank, click OK
   - Expected: Warning dialog "Please enter a license key"

2. **Whitespace only**:
   - Enter "   " (spaces), click OK
   - Expected: Invalid license error

3. **Wrong format**:
   - Enter "METAL-DEMO-1234"
   - Expected: Invalid license error with format examples

4. **Cancel activation**:
   - Open activation dialog, click Cancel
   - Expected: Nothing happens, license unchanged

5. **Cancel deactivation**:
   - Open deactivation dialog, click Cancel
   - Expected: License remains active

### 15. Performance Check ✅

**Test**: Plugin doesn't slow down IDE

- Check IDE startup time (should be < 2s difference)
- Check memory usage (should be < 50MB)
- Check CPU usage (idle when not using Metal files)

---

## Automated Testing Commands

```bash
# Full build and verification
./gradlew clean build verifyPlugin

# Build plugin only
./gradlew buildPlugin

# Run plugin verifier
./gradlew runPluginVerifier

# Check for deprecation warnings
./gradlew compileKotlin 2>&1 | grep -i deprecat
```

---

## Bug Report Template

If you find issues, report with:

```markdown
**Version**: 1.0.3
**IDE**: IntelliJ IDEA 2024.2.5
**OS**: macOS 15.0 / Windows 11 / Ubuntu 22.04

**Steps to Reproduce**:
1. 
2. 
3. 

**Expected**: 
**Actual**: 

**License Tier**: FREE / PRO / ENTERPRISE

**Logs**: (Help → Show Log in Finder)
```

---

## Test License Keys

For testing only (will be replaced with real validation):

```
FREE (no key needed)
PRO:        METAL-PRO-1234-5678-ABCD
ENTERPRISE: METAL-ENT-9999-8888-WXYZ
```

---

## Success Criteria ✅

All tests should pass:
- [x] Installation successful
- [x] Menu structure correct
- [x] License activation works
- [x] License persistence works
- [x] License deactivation works
- [x] Notifications appear
- [x] Pro features respect license
- [x] All file types recognized
- [x] No performance issues
- [x] Error handling robust

---

**Test Date**: November 16, 2025
**Tester**: Jakub Jirák
**Status**: ✅ ALL TESTS PASSED

© 2025 Jakub Jirák. Proprietary software.
