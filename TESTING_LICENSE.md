# License System Testing Guide

## Quick Test Procedure

### Test 1: Initial State (FREE Tier)
1. ✅ Install plugin
2. ✅ Open any `.metal` file
3. ✅ Right-click in editor
4. ✅ Check "Metal Shader Support" submenu
5. **Expected**: All Pro features should be DISABLED (grayed out)

### Test 2: License Activation
1. ✅ Go to menu: **Tools → Metal Shader Support → Activate License**
2. ✅ Enter license key: `METAL-ENT-9999-8888-WXYZ`
3. ✅ Click OK
4. **Expected**: Success notification appears

### Test 3: Menu State After Activation
1. ✅ Right-click in the same Metal file
2. ✅ Check "Metal Shader Support" submenu
3. **Expected**: All Pro features should now be ENABLED
   - ✅ Validate Shader
   - ✅ Optimize Shader  
   - ✅ Profile Shader Performance
   - ✅ Analyze Shader Complexity

### Test 4: Feature Access
1. ✅ Click on any Pro feature (e.g., "Validate Shader")
2. **Expected**: Feature executes (shows validation results)
3. **Expected**: NO "upgrade to Pro" popup

### Test 5: License Persistence
1. ✅ Close IntelliJ IDEA
2. ✅ Restart IDE
3. ✅ Open Metal file
4. ✅ Check menu items
5. **Expected**: Pro features still ENABLED

### Test 6: Deactivation
1. ✅ Go to: **Tools → Metal Shader Support → Deactivate License**
2. ✅ Confirm deactivation
3. ✅ Check menu items
4. **Expected**: Pro features should be DISABLED again

## Test License Keys

### PRO Tier
```
METAL-PRO-1234-5678-ABCD
METAL-PRO-0000-1111-TEST
```

### ENTERPRISE Tier
```
METAL-ENT-9999-8888-WXYZ
METAL-ENT-5555-6666-DEMO
```

### Invalid (should fail)
```
INVALID-KEY-1234
METAL-FREE-0000-0000-0000
```

## Expected Behaviors

| State | Menu Items Enabled | Click Behavior |
|-------|-------------------|----------------|
| FREE | ❌ Disabled | Shows upgrade dialog |
| PRO | ✅ Enabled | Executes feature |
| ENTERPRISE | ✅ Enabled | Executes feature |

## Troubleshooting

### Menu items stay disabled after activation
**Solution**: This is now FIXED in v1.0.4. If still occurring:
1. Check IDE logs: `Help → Show Log in Finder`
2. Look for `MetalLicenseManager` errors
3. Try: `Tools → Metal Shader Support → License Info`

### License doesn't persist
**Check**: File should exist at:
```
~/Library/Application Support/JetBrains/[IdeaVersion]/options/metalShaderLicense.xml
```

### Pro features show upgrade popup despite license
**Solution**: 
1. Deactivate license
2. Restart IDE
3. Re-activate license

## Debug Commands

### Check Current License State
```kotlin
// Developer Console (Cmd+Shift+A → "IDE Scripting Console")
import com.metalshader.licensing.MetalLicenseManager

val manager = MetalLicenseManager.getInstance()
println("Tier: ${manager.getCurrentTier()}")
println("Pro Enabled: ${manager.isProFeatureEnabled()}")
println("License Info: ${manager.getLicenseInfo()}")
```

### Force License Refresh
```kotlin
import com.intellij.openapi.application.ApplicationManager
import com.metalshader.licensing.LicenseChangeListener

ApplicationManager.getApplication().messageBus
    .syncPublisher(LicenseChangeListener.TOPIC)
    .licenseChanged(manager.getCurrentTier())
```

## Verification Checklist

- [ ] Install fresh plugin
- [ ] Verify FREE tier restrictions
- [ ] Activate PRO license
- [ ] Verify menu items enabled
- [ ] Test actual feature execution
- [ ] Restart IDE
- [ ] Verify persistence
- [ ] Deactivate license
- [ ] Verify FREE tier again

---

**All tests should pass with version 1.0.4+**
