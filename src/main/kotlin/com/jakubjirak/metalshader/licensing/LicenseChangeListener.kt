package com.jakubjirak.metalshader.licensing

import com.intellij.util.messages.Topic

interface LicenseChangeListener {
    
    companion object {
        val TOPIC = Topic.create("MetalShaderLicenseChanged", LicenseChangeListener::class.java)
    }
    
    fun licenseChanged(newTier: MetalLicenseManager.LicenseTier)
}
