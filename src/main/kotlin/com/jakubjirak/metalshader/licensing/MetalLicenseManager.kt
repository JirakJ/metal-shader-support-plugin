package com.jakubjirak.metalshader.licensing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "MetalShaderLicense",
    storages = [Storage("metalShaderLicense.xml")]
)
class MetalLicenseManager : PersistentStateComponent<MetalLicenseManager.State> {
    
    data class State(
        var licenseKey: String = "",
        var activationDate: Long = 0,
        var tier: LicenseTier = LicenseTier.FREE
    )
    
    enum class LicenseTier {
        FREE,
        PRO,
        ENTERPRISE
    }
    
    private var state = State()
    
    companion object {
        fun getInstance(): MetalLicenseManager {
            return ApplicationManager.getApplication().getService(MetalLicenseManager::class.java)
        }
    }
    
    override fun getState(): State = state
    
    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }
    
    fun getCurrentTier(): LicenseTier = state.tier
    
    fun isProFeatureEnabled(): Boolean {
        return state.tier == LicenseTier.PRO || state.tier == LicenseTier.ENTERPRISE
    }
    
    fun hasProLicense(): Boolean = isProFeatureEnabled()
    
    fun activateLicense(licenseKey: String): Boolean {
        // Simple validation - in production use proper license validation
        val newTier = when {
            licenseKey.startsWith("METAL-PRO-") -> LicenseTier.PRO
            licenseKey.startsWith("METAL-ENT-") -> LicenseTier.ENTERPRISE
            else -> return false
        }
        
        state.licenseKey = licenseKey
        state.activationDate = System.currentTimeMillis()
        state.tier = newTier
        
        // Notify listeners about license change
        ApplicationManager.getApplication().messageBus
            .syncPublisher(com.jakubjirak.metalshader.listeners.LicenseChangeListener.TOPIC)
            .licenseChanged(newTier.name)
        
        return true
    }
    
    fun deactivateLicense() {
        state.licenseKey = ""
        state.activationDate = 0
        state.tier = LicenseTier.FREE
        
        // Notify listeners about license change
        ApplicationManager.getApplication().messageBus
            .syncPublisher(com.jakubjirak.metalshader.listeners.LicenseChangeListener.TOPIC)
            .licenseChanged(LicenseTier.FREE.name)
    }
    
    fun getLicenseInfo(): String {
        return when (state.tier) {
            LicenseTier.FREE -> "Free Tier - Basic features"
            LicenseTier.PRO -> "Pro Tier - All features unlocked"
            LicenseTier.ENTERPRISE -> "Enterprise Tier - All features + priority support"
        }
    }
    
    fun hasProFeatures(): Boolean {
        return state.tier == LicenseTier.PRO || state.tier == LicenseTier.ENTERPRISE
    }
    
    fun hasEnterpriseFeatures(): Boolean {
        return state.tier == LicenseTier.ENTERPRISE
    }
    
    fun validateAndActivateLicense(licenseKey: String): Boolean {
        return activateLicense(licenseKey)
    }
}
