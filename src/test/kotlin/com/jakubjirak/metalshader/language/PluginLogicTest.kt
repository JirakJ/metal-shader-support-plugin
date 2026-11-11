/*
 * Copyright (c) 2026 Jakub Jirák.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.language

import com.jakubjirak.metalshader.feedback.FeedbackNotifier
import com.jakubjirak.metalshader.feedback.FeedbackState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class PluginLogicTest {

    private val pluginXml: String = javaClass.getResource("/META-INF/plugin.xml")!!.readText()

    @Test
    fun `structure view labels name shader functions and structs`() {
        assertEquals("⚡ addArrays", presentableText("kernel void addArrays(device float* a [[buffer(0)]])"))
        assertEquals("🔺 vertexMain", presentableText("vertex VertexOut vertexMain(uint vid [[vertex_id]])"))
        assertEquals("🎨 fragmentMain", presentableText("fragment float4 fragmentMain(VertexOut in [[stage_in]])"))
        assertEquals("📦 Uniforms", presentableText("struct Uniforms { float4x4 mvp; };"))
        assertEquals("⚡ kernel", presentableText("kernel"))
        assertEquals("💬 Comment", presentableText("// just a comment"))
        assertNull(presentableText("float x = 1.0;"))
    }

    @Test
    fun `metal file type only claims metal and msl extensions`() {
        val match = Regex("""extensions="([^"]+)"""").find(pluginXml)!!
        assertEquals("metal;msl", match.groupValues[1], "C/C++/Objective-C headers and .mm files must not be hijacked")
    }

    @Test
    fun `removed simulated tool windows and settings stay removed`() {
        listOf("Metal Shader Preview", "Metal Debugger", "Metal Profiler", "MetalShaderPreviewService", "MetalShaderSettings", "60 FPS")
            .forEach { assertFalse(pluginXml.contains(it), it) }
        assertTrue(pluginXml.contains("<notificationGroup id=\"${FeedbackNotifier.NOTIFICATION_GROUP_ID}\""))
    }

    @Test
    fun `feedback prompt waits a week and is shown once`() {
        val day = TimeUnit.DAYS.toMillis(1)
        val now = 1_700_000_000_000L
        val fresh = FeedbackState.State()
        assertFalse(FeedbackNotifier.shouldShowFeedback(fresh, now))
        assertEquals(now, fresh.firstActivation)
        assertFalse(FeedbackNotifier.shouldShowFeedback(FeedbackState.State(now), now + 7 * day - 1))
        assertTrue(FeedbackNotifier.shouldShowFeedback(FeedbackState.State(now), now + 7 * day))
        assertFalse(FeedbackNotifier.shouldShowFeedback(FeedbackState.State(now, feedbackShown = true), now + 30 * day))
        assertFalse(FeedbackNotifier.shouldShowFeedback(FeedbackState.State(now, dismissed = true), now + 30 * day))
    }
}
