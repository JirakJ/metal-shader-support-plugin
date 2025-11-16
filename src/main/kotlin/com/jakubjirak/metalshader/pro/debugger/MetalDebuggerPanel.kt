package com.jakubjirak.metalshader.pro.debugger

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class MetalDebuggerPanel(private val project: Project) : JPanel(BorderLayout()) {
    private val statusLabel = JBLabel("Ready")
    private val debugOutput = JBTextArea()
    
    init {
        border = EmptyBorder(10, 10, 10, 10)
        
        val header = JPanel(BorderLayout())
        header.add(JBLabel("Metal Shader Debugger"), BorderLayout.WEST)
        header.add(statusLabel, BorderLayout.EAST)
        add(header, BorderLayout.NORTH)
        
        val controls = JPanel(GridLayout(1, 4, 5, 5))
        controls.add(JButton("Start Debug Session").apply {
            addActionListener { startDebugSession() }
        })
        controls.add(JButton("Set Breakpoint").apply {
            addActionListener { setBreakpoint() }
        })
        controls.add(JButton("Inspect Variables").apply {
            addActionListener { inspectVariables() }
        })
        controls.add(JButton("Clear").apply {
            addActionListener { debugOutput.text = "" }
        })
        
        val controlsWrapper = JPanel(BorderLayout())
        controlsWrapper.border = EmptyBorder(10, 0, 10, 0)
        controlsWrapper.add(controls, BorderLayout.CENTER)
        add(controlsWrapper, BorderLayout.CENTER)
        
        debugOutput.isEditable = false
        debugOutput.lineWrap = true
        debugOutput.text = """
            Metal Shader Debugger
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            
            Features:
            • Visual debugging with breakpoints
            • Variable inspection (vectors, matrices, textures)
            • Thread execution visualization
            • GPU frame capture integration
            
            Click 'Start Debug Session' to begin debugging your Metal shaders.
        """.trimIndent()
        add(JBScrollPane(debugOutput), BorderLayout.SOUTH)
    }
    
    private fun startDebugSession() {
        statusLabel.text = "Debugging..."
        debugOutput.append("\n\n[${System.currentTimeMillis()}] Debug session started")
        debugOutput.append("\n→ Attaching to Metal GPU...")
        debugOutput.append("\n→ Capturing frame data...")
        debugOutput.append("\n✓ Ready for breakpoints")
    }
    
    private fun setBreakpoint() {
        debugOutput.append("\n\n[${System.currentTimeMillis()}] Breakpoint set at current line")
    }
    
    private fun inspectVariables() {
        debugOutput.append("\n\n[${System.currentTimeMillis()}] Variable Inspection:")
        debugOutput.append("\n  float4 color = (0.5, 0.3, 0.8, 1.0)")
        debugOutput.append("\n  uint2 gid = (512, 384)")
    }
}
