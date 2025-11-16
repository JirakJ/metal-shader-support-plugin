package com.jakubjirak.metalshader.listeners

import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic

interface LicenseChangeListener {
    companion object {
        val TOPIC = Topic.create("MetalLicenseChanged", LicenseChangeListener::class.java)
    }

    fun licenseChanged(licenseType: String)
}

class LicenseChangeNotifier(private val project: Project) : LicenseChangeListener {
    override fun licenseChanged(licenseType: String) {
        project.messageBus.syncPublisher(LicenseChangeListener.TOPIC).licenseChanged(licenseType)
    }
}
