// ============================================================================
// TEST FILE - Metal Shader Support Plugin
// Try these features:
// 1. Hover over "kernel" below to see documentation
// 2. Type "kernel" + Tab for instant template
// 3. Press Cmd+F12 (Mac) / Ctrl+F12 (Win) to see structure
// 4. Right-click on "testKernel" and select "Find Usages"
// 5. Fold this comment block (click - in gutter)
// ============================================================================

#include <metal_stdlib>
using namespace metal;

// Hover over "kernel" to see tooltip!
kernel void testKernel(
    texture2d<float, access::read> inTexture [[texture(0)]],
    texture2d<float, access::write> outTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    // This function should appear in Structure View with ⚡ icon
    
    if (gid.x >= outTexture.get_width() || gid.y >= outTexture.get_height()) {
        return;
    }
    
    // Hover over "read" to see documentation
    float4 color = inTexture.read(gid);
    
    // Hover over "dot" to see documentation  
    float luminance = dot(color.rgb, float3(0.299, 0.587, 0.114));
    
    // Hover over "write" to see documentation
    outTexture.write(float4(luminance), gid);
}

// Try typing "vertex" + Tab here to insert template:


// Try typing "fragment" + Tab here:


// Try creating new file: Right-click → New → Metal Shader
