/*
 * Copyright (c) 2024-2025 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * Persistent project-level settings for the Metal Shader Support plugin.
 * Stores configuration for shader validation, preview, profiling, and transpilation.
 */
@State(name = "MetalShaderSettings", storages = [Storage("metal-shader-settings.xml")])
class MetalShaderSettings : PersistentStateComponent<MetalShaderSettings.State> {

    private var myState = State()

    class State {
        var maxThreadgroupSize: Int = 1024
        var enableRealtimePreview: Boolean = true
        var previewFpsCap: Int = 60
        var transpilerTarget: String = "SPIR-V"
        var enablePerformanceInspections: Boolean = true
    }

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    var maxThreadgroupSize: Int
        get() = myState.maxThreadgroupSize
        set(value) { myState.maxThreadgroupSize = value }

    var enableRealtimePreview: Boolean
        get() = myState.enableRealtimePreview
        set(value) { myState.enableRealtimePreview = value }

    var previewFpsCap: Int
        get() = myState.previewFpsCap
        set(value) { myState.previewFpsCap = value }

    var transpilerTarget: String
        get() = myState.transpilerTarget
        set(value) { myState.transpilerTarget = value }

    var enablePerformanceInspections: Boolean
        get() = myState.enablePerformanceInspections
        set(value) { myState.enablePerformanceInspections = value }

    companion object {
        fun getInstance(project: Project): MetalShaderSettings =
            project.getService(MetalShaderSettings::class.java)
    }
}
