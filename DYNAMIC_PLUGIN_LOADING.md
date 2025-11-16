# Dynamic Plugin Loading - No Restart Required! 🚀

## Overview

**Metal Shader Support** plugin now supports **dynamic loading** - you can activate/deactivate licenses and update the plugin **without restarting your IDE**!

## What This Means

✅ **Install plugin** → Works immediately (no restart)  
✅ **Activate license** → Features unlock instantly (no restart)  
✅ **Deactivate license** → Changes apply immediately (no restart)  
✅ **Update plugin** → New features available instantly (no restart)  

## Technical Implementation

### 1. Dynamic Plugin Listener

```kotlin
class PluginLifecycleListener : DynamicPluginListener {
    override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
        // Initialize services dynamically when plugin loads
    }
    
    override fun beforePluginUnload(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
        // Cleanup before unload
    }
}
```

**Registered in `plugin.xml`:**
```xml
<applicationListeners>
    <listener class="com.jirakj.metalshadersupport.listeners.PluginLifecycleListener"
              topic="com.intellij.ide.plugins.DynamicPluginListener"/>
</applicationListeners>
```

### 2. License Change Listener

```kotlin
interface LicenseChangeListener {
    companion object {
        val TOPIC = Topic.create("MetalShaderSupport.LicenseChanged", LicenseChangeListener::class.java)
    }
    
    fun licenseChanged(newTier: String)
}
```

When license is activated:
```kotlin
ApplicationManager.getApplication().messageBus
    .syncPublisher(LicenseChangeListener.TOPIC)
    .licenseChanged(tier)
```

Actions automatically update their enabled state without restart!

### 3. Key Components

#### Plugin Lifecycle
- `PluginLifecycleListener` - Handles plugin load/unload events
- No static initialization - everything loads dynamically

#### License Management
- `LicenseChangeListener.TOPIC` - Message bus for license events
- Actions subscribe to license changes
- Menus update automatically via ActionManager

#### Action Updates
```kotlin
override fun licenseChanged(newTier: String) {
    val actionManager = ActionManager.getInstance()
    
    // Force action system to update
    listOf(
        "MetalShaderSupport.LivePreview",
        "MetalShaderSupport.PerformanceAnalysis",
        "MetalShaderSupport.CrossPlatformExport"
    ).forEach { actionId ->
        actionManager.getAction(actionId)?.templatePresentation?.isEnabled = true
    }
}
```

## User Experience

### Before (Traditional Plugin)
1. Install plugin
2. **Restart IDE** ⏰
3. Activate license
4. **Restart IDE** ⏰
5. Update plugin
6. **Restart IDE** ⏰

**Total: 3 restarts = ~3-5 minutes lost**

### Now (Dynamic Plugin)
1. Install plugin ✨ (works immediately)
2. Activate license ✨ (features unlock instantly)
3. Update plugin ✨ (new version active immediately)

**Total: 0 restarts = 0 minutes lost** 🎉

## Testing

### Test Dynamic License Activation
1. Open IDE
2. Tools → Metal Shader Support → Activate License
3. Enter license key
4. **Verify**: Pro features immediately available (no restart!)

### Test Plugin Update
1. Install plugin v1.0.4
2. Update to v1.0.5
3. **Verify**: New features work immediately (no restart!)

### Test Plugin Unload
1. Disable plugin in settings
2. **Verify**: Plugin unloads cleanly (no restart!)

## Benefits

### For Users
- ⚡ **Instant activation** - License works immediately
- 🔄 **Zero downtime** - No workflow interruption
- 🎯 **Better UX** - Seamless experience

### For Development
- 🚀 **Faster testing** - No restart between changes
- 🔧 **Easier debugging** - Dynamic reload during development
- 📦 **Better updates** - Users get features instantly

## Compatibility

- ✅ **IntelliJ Platform 2024.2+** (build 242+)
- ✅ **All JetBrains IDEs** (IntelliJ IDEA, CLion, AppCode, etc.)
- ✅ **macOS** (primary platform for Metal)

## Known Limitations

### What Requires Restart
- ❌ Changing plugin version below 242 (old IDE versions)
- ❌ Modifying core plugin XML structure (rare)
- ❌ Changing plugin dependencies

### What Works Dynamically
- ✅ License activation/deactivation
- ✅ Action enable/disable
- ✅ Settings changes
- ✅ Plugin updates
- ✅ Feature toggles

## Architecture Notes

### Message Bus Pattern
IntelliJ Platform uses a message bus for decoupled communication:
```kotlin
// Publisher (License Manager)
messageBus.syncPublisher(TOPIC).licenseChanged(tier)

// Subscriber (Action Manager)
messageBus.connect().subscribe(TOPIC, object : LicenseChangeListener {
    override fun licenseChanged(newTier: String) { /* update UI */ }
})
```

### Action Manager Integration
```kotlin
val actionManager = ActionManager.getInstance()
actionManager.getAction("MyAction")?.templatePresentation?.isEnabled = true
```

Platform automatically updates:
- Menu items
- Toolbar buttons
- Context menus
- Keyboard shortcuts

## Debugging

### Enable Dynamic Plugin Logging
Add to `idea.log`:
```
#com.intellij.ide.plugins.DynamicPluginListener:trace
```

### Check Plugin Status
```kotlin
val pluginId = PluginId.getId("com.jirakj.metalshadersupport")
val plugin = PluginManager.getPlugin(pluginId)
println("Dynamic: ${plugin.isDynamic}")
```

### Monitor License Changes
```kotlin
ApplicationManager.getApplication().messageBus.connect().subscribe(
    LicenseChangeListener.TOPIC,
    object : LicenseChangeListener {
        override fun licenseChanged(newTier: String) {
            println("License changed to: $newTier")
        }
    }
)
```

## Future Enhancements

### Planned
- [ ] Hot-reload shader code without compilation
- [ ] Dynamic theme switching for shader preview
- [ ] Live settings sync across IDE instances
- [ ] Real-time feature flag updates

### Under Consideration
- [ ] Remote license activation (API-based)
- [ ] Team license synchronization
- [ ] A/B testing for Pro features
- [ ] Usage analytics (opt-in)

## Resources

- [JetBrains: Dynamic Plugins](https://plugins.jetbrains.com/docs/intellij/dynamic-plugins.html)
- [IntelliJ Platform SDK: Message Bus](https://plugins.jetbrains.com/docs/intellij/messaging-infrastructure.html)
- [Action System](https://plugins.jetbrains.com/docs/intellij/basic-action-system.html)

---

**© 2025 Jakub Jirák. All rights reserved.**
