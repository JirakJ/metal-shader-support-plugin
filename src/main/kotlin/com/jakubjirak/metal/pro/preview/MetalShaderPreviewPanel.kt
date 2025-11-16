// Copyright (c) 2025 Jakub Jirák. All rights reserved.
package com.jakubjirak.metal.pro.preview

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import kotlin.math.sin

class MetalShaderPreviewPanel(
    private val project: Project,
    private val editor: Editor
) : JBPanel<MetalShaderPreviewPanel>(BorderLayout()), Disposable {
    
    private val renderPanel = ShaderRenderPanel()
    private val statusLabel = JBLabel("Preview: Ready")
    private val fpsLabel = JBLabel("60 FPS")
    private val gpuTimeLabel = JBLabel("GPU: 0.5ms")
    
    private var isRendering = false
    private var animationTimer: Timer? = null
    private var frameCount = 0
    private var lastFpsUpdate = System.currentTimeMillis()
    
    init {
        setupUI()
        startRenderLoop()
        
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                renderPanel.revalidate()
                renderPanel.repaint()
            }
        })
    }
    
    private fun setupUI() {
        preferredSize = Dimension(400, 600)
        background = JBColor.PanelBackground
        
        // Toolbar
        val toolbar = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = JBUI.Borders.empty(8)
            
            add(JBLabel("Metal Shader Preview").apply {
                font = font.deriveFont(font.size + 2f)
            })
            add(Box.createHorizontalGlue())
            add(fpsLabel)
            add(Box.createHorizontalStrut(10))
            add(gpuTimeLabel)
        }
        
        // Render area
        renderPanel.preferredSize = Dimension(400, 400)
        renderPanel.border = JBUI.Borders.customLine(JBColor.border(), 1)
        
        val renderWrapper = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(8)
            add(renderPanel, BorderLayout.CENTER)
        }
        
        // Status bar
        val statusBar = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(4, 8)
            add(statusLabel, BorderLayout.WEST)
        }
        
        add(toolbar, BorderLayout.NORTH)
        add(renderWrapper, BorderLayout.CENTER)
        add(statusBar, BorderLayout.SOUTH)
    }
    
    private fun startRenderLoop() {
        isRendering = true
        animationTimer = Timer(16) { // ~60 FPS
            renderPanel.updateFrame()
            renderPanel.repaint()
            
            frameCount++
            val now = System.currentTimeMillis()
            if (now - lastFpsUpdate > 1000) {
                val fps = frameCount * 1000.0 / (now - lastFpsUpdate)
                fpsLabel.text = String.format("%.1f FPS", fps)
                frameCount = 0
                lastFpsUpdate = now
            }
        }
        animationTimer?.start()
    }
    
    fun updateShaderCode(code: String) {
        renderPanel.shaderCode = code
        statusLabel.text = "Preview: Compiling..."
        
        // Simulate compilation delay
        Timer(100) {
            statusLabel.text = "Preview: Active"
            renderPanel.repaint()
        }.apply {
            isRepeats = false
            start()
        }
    }
    
    override fun dispose() {
        isRendering = false
        animationTimer?.stop()
        animationTimer = null
    }
    
    private inner class ShaderRenderPanel : JPanel() {
        var shaderCode: String = ""
        private var time = 0f
        
        init {
            background = JBColor.BLACK
        }
        
        fun updateFrame() {
            time += 0.016f // 16ms per frame
        }
        
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            
            val w = width
            val h = height
            
            // Procedural shader-like effect
            for (y in 0 until h step 2) {
                for (x in 0 until w step 2) {
                    val nx = x.toFloat() / w
                    val ny = y.toFloat() / h
                    
                    // Simple animated pattern
                    val r = (sin(nx * 10 + time) * 0.5 + 0.5) * 255
                    val g = (sin(ny * 10 + time * 1.2) * 0.5 + 0.5) * 255
                    val b = (sin((nx + ny) * 5 + time * 0.8) * 0.5 + 0.5) * 255
                    
                    g2d.color = java.awt.Color(r.toInt(), g.toInt(), b.toInt())
                    g2d.fillRect(x, y, 2, 2)
                }
            }
            
            // Overlay info
            g2d.color = JBColor.WHITE
            g2d.drawString("Shader Preview (Simulated)", 10, 20)
            g2d.drawString("Time: ${String.format("%.2f", time)}s", 10, 40)
            
            if (shaderCode.isNotEmpty()) {
                val lines = shaderCode.lines().take(3)
                lines.forEachIndexed { index, line ->
                    g2d.drawString(line.take(40), 10, h - 60 + index * 15)
                }
            }
        }
    }
}
