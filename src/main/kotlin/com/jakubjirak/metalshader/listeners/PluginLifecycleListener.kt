package com.jakubjirak.metalshader.listeners

import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.jakubjirak.metalshader.licensing.MetalLicenseManager

class PluginLifecycleListener : DynamicPluginListener {
    override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
        if (pluginDescriptor.pluginId.idString == "com.jakubjirak.metalshader") {
            ApplicationManager.getApplication().getService(MetalLicenseManager::class.java)
        }
    }

    override fun beforePluginUnload(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
        if (pluginDescriptor.pluginId.idString == "com.jakubjirak.metalshader") {
            // Cleanup if needed
        }
    }
}
