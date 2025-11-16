//
// metal_performance_shaders.metal
// Metal Performance Shaders (MPS) Integration Examples
//
// Demonstrates:
// - Custom shaders compatible with MPS
// - Image processing kernels
// - Neural network layer implementations
// - Performance optimization patterns
//

#include <metal_stdlib>
using namespace metal;

// ============================================================================
// IMAGE PROCESSING KERNELS (MPS-Compatible)
// ============================================================================

// Custom Gaussian Blur (compatible with MPSImageGaussianBlur)
kernel void customGaussianBlur(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    constant float& sigma [[buffer(0)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    const int radius = int(ceil(3.0 * sigma));
    half4 sum = half4(0.0h);
    half weightSum = 0.0h;
    
    for (int y = -radius; y <= radius; y++) {
        for (int x = -radius; x <= radius; x++) {
            int2 offset = int2(x, y);
            uint2 coord = uint2(int2(gid) + offset);
            
            if (coord.x < inputTexture.get_width() && coord.y < inputTexture.get_height()) {
                float dist = length(float2(x, y));
                half weight = half(exp(-dist * dist / (2.0 * sigma * sigma)));
                
                sum += inputTexture.read(coord) * weight;
                weightSum += weight;
            }
        }
    }
    
    outputTexture.write(sum / weightSum, gid);
}

// Edge Detection (Sobel) - MPS-style
kernel void sobelEdgeDetection(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    // Sobel kernels
    const half3x3 Gx = half3x3(
        half3(-1.0h, 0.0h, 1.0h),
        half3(-2.0h, 0.0h, 2.0h),
        half3(-1.0h, 0.0h, 1.0h)
    );
    
    const half3x3 Gy = half3x3(
        half3(-1.0h, -2.0h, -1.0h),
        half3( 0.0h,  0.0h,  0.0h),
        half3( 1.0h,  2.0h,  1.0h)
    );
    
    half3 gx = half3(0.0h);
    half3 gy = half3(0.0h);
    
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            uint2 coord = uint2(int2(gid) + int2(x, y));
            coord = clamp(coord, uint2(0), uint2(inputTexture.get_width() - 1, inputTexture.get_height() - 1));
            
            half3 color = inputTexture.read(coord).rgb;
            gx += color * Gx[y + 1][x + 1];
            gy += color * Gy[y + 1][x + 1];
        }
    }
    
    half magnitude = length(half2(length(gx), length(gy)));
    outputTexture.write(half4(magnitude), gid);
}

// Histogram Equalization Kernel
kernel void histogramEqualization(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    constant uint* histogram [[buffer(0)]],
    constant uint* cdf [[buffer(1)]],  // Cumulative distribution function
    constant uint& totalPixels [[buffer(2)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    half4 color = inputTexture.read(gid);
    
    // Equalize each channel
    for (int c = 0; c < 3; c++) {
        uint bin = uint(color[c] * 255.0h);
        half equalizedValue = half(cdf[bin]) / half(totalPixels);
        color[c] = equalizedValue;
    }
    
    outputTexture.write(color, gid);
}

// ============================================================================
// NEURAL NETWORK LAYER IMPLEMENTATIONS
// ============================================================================

// Convolution Layer (like MPSCNNConvolution)
kernel void convolution2D(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    constant half* weights [[buffer(0)]],
    constant half* biases [[buffer(1)]],
    constant uint2& kernelSize [[buffer(2)]],
    constant uint& stride [[buffer(3)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    uint2 inputCoord = gid * stride;
    half4 sum = half4(biases[0]);
    
    for (uint y = 0; y < kernelSize.y; y++) {
        for (uint x = 0; x < kernelSize.x; x++) {
            uint2 coord = inputCoord + uint2(x, y);
            if (coord.x < inputTexture.get_width() && coord.y < inputTexture.get_height()) {
                half4 input = inputTexture.read(coord);
                uint weightIndex = y * kernelSize.x + x;
                sum += input * weights[weightIndex];
            }
        }
    }
    
    outputTexture.write(sum, gid);
}

// ReLU Activation (like MPSCNNNeuronReLU)
kernel void reluActivation(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    half4 value = inputTexture.read(gid);
    value = max(value, half4(0.0h));
    outputTexture.write(value, gid);
}

// Max Pooling (like MPSCNNPoolingMax)
kernel void maxPooling(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    constant uint2& poolSize [[buffer(0)]],
    constant uint& stride [[buffer(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    uint2 inputCoord = gid * stride;
    half4 maxVal = half4(-INFINITY);
    
    for (uint y = 0; y < poolSize.y; y++) {
        for (uint x = 0; x < poolSize.x; x++) {
            uint2 coord = inputCoord + uint2(x, y);
            if (coord.x < inputTexture.get_width() && coord.y < inputTexture.get_height()) {
                half4 value = inputTexture.read(coord);
                maxVal = max(maxVal, value);
            }
        }
    }
    
    outputTexture.write(maxVal, gid);
}

// Batch Normalization (like MPSCNNBatchNormalization)
kernel void batchNormalization(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    constant half* mean [[buffer(0)]],
    constant half* variance [[buffer(1)]],
    constant half* gamma [[buffer(2)]],
    constant half* beta [[buffer(3)]],
    constant half& epsilon [[buffer(4)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    half4 input = inputTexture.read(gid);
    
    // Normalize: (x - mean) / sqrt(variance + epsilon)
    half4 normalized = (input - half4(mean[0])) / sqrt(half4(variance[0]) + half(epsilon));
    
    // Scale and shift: gamma * normalized + beta
    half4 output = half4(gamma[0]) * normalized + half4(beta[0]);
    
    outputTexture.write(output, gid);
}

// ============================================================================
// MATRIX OPERATIONS (MPS Matrix-style)
// ============================================================================

// Matrix Multiplication (GEMM) - Tiled version
kernel void matrixMultiply(
    device const float* A [[buffer(0)]],
    device const float* B [[buffer(1)]],
    device float* C [[buffer(2)]],
    constant uint& M [[buffer(3)]],  // A rows
    constant uint& K [[buffer(4)]],  // A cols / B rows
    constant uint& N [[buffer(5)]],  // B cols
    uint2 gid [[thread_position_in_grid]],
    uint2 tid [[thread_position_in_threadgroup]],
    uint2 tgSize [[threads_per_threadgroup]]
) {
    const uint TILE_SIZE = 16;
    
    threadgroup float As[TILE_SIZE][TILE_SIZE];
    threadgroup float Bs[TILE_SIZE][TILE_SIZE];
    
    uint row = gid.y;
    uint col = gid.x;
    
    float sum = 0.0;
    
    for (uint tile = 0; tile < (K + TILE_SIZE - 1) / TILE_SIZE; tile++) {
        // Load tile into shared memory
        uint aCol = tile * TILE_SIZE + tid.x;
        uint bRow = tile * TILE_SIZE + tid.y;
        
        As[tid.y][tid.x] = (row < M && aCol < K) ? A[row * K + aCol] : 0.0;
        Bs[tid.y][tid.x] = (bRow < K && col < N) ? B[bRow * N + col] : 0.0;
        
        threadgroup_barrier(mem_flags::mem_threadgroup);
        
        // Compute partial dot product
        for (uint k = 0; k < TILE_SIZE; k++) {
            sum += As[tid.y][k] * Bs[k][tid.x];
        }
        
        threadgroup_barrier(mem_flags::mem_threadgroup);
    }
    
    if (row < M && col < N) {
        C[row * N + col] = sum;
    }
}

// SoftMax (like MPSMatrixSoftMax)
kernel void softmax(
    device const float* input [[buffer(0)]],
    device float* output [[buffer(1)]],
    constant uint& length [[buffer(2)]],
    uint gid [[thread_position_in_grid]]
) {
    // Find max for numerical stability
    float maxVal = -INFINITY;
    for (uint i = 0; i < length; i++) {
        maxVal = max(maxVal, input[gid * length + i]);
    }
    
    // Compute exp and sum
    float sum = 0.0;
    for (uint i = 0; i < length; i++) {
        float expVal = exp(input[gid * length + i] - maxVal);
        output[gid * length + i] = expVal;
        sum += expVal;
    }
    
    // Normalize
    for (uint i = 0; i < length; i++) {
        output[gid * length + i] /= sum;
    }
}

// ============================================================================
// PERFORMANCE OPTIMIZATION PATTERNS
// ============================================================================

// Vectorized operations for better performance
kernel void vectorizedImageProcessing(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    constant half4& colorTransform [[buffer(0)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outputTexture.get_width() || gid.y >= outputTexture.get_height()) {
        return;
    }
    
    // Process 4 pixels at once using SIMD
    half4 color = inputTexture.read(gid);
    
    // Vectorized operations are automatically optimized
    color = color * colorTransform;
    color = saturate(color);
    
    outputTexture.write(color, gid);
}

// Threadgroup memory optimization
kernel void threadgroupOptimizedFilter(
    texture2d<half, access::read> inputTexture [[texture(0)]],
    texture2d<half, access::write> outputTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]],
    uint2 tid [[thread_position_in_threadgroup]],
    uint2 tgSize [[threads_per_threadgroup]]
) {
    const uint TILE_SIZE = 16;
    const uint FILTER_RADIUS = 1;
    const uint SHARED_SIZE = TILE_SIZE + 2 * FILTER_RADIUS;
    
    threadgroup half4 sharedMem[SHARED_SIZE][SHARED_SIZE];
    
    // Load data into shared memory
    for (uint y = tid.y; y < SHARED_SIZE; y += tgSize.y) {
        for (uint x = tid.x; x < SHARED_SIZE; x += tgSize.x) {
            int2 coord = int2(gid) - int2(FILTER_RADIUS) + int2(x, y);
            coord = clamp(coord, int2(0), int2(inputTexture.get_width() - 1, inputTexture.get_height() - 1));
            sharedMem[y][x] = inputTexture.read(uint2(coord));
        }
    }
    
    threadgroup_barrier(mem_flags::mem_threadgroup);
    
    // Process using shared memory (much faster!)
    if (gid.x < outputTexture.get_width() && gid.y < outputTexture.get_height()) {
        half4 sum = half4(0.0h);
        
        for (int y = -FILTER_RADIUS; y <= FILTER_RADIUS; y++) {
            for (int x = -FILTER_RADIUS; x <= FILTER_RADIUS; x++) {
                sum += sharedMem[tid.y + FILTER_RADIUS + y][tid.x + FILTER_RADIUS + x];
            }
        }
        
        sum /= half((2 * FILTER_RADIUS + 1) * (2 * FILTER_RADIUS + 1));
        outputTexture.write(sum, gid);
    }
}

// ============================================================================
// USAGE NOTES
// ============================================================================

/*
 * Integration with Metal Performance Shaders:
 *
 * 1. Image Filters:
 *    - Use MPSImageGaussianBlur for standard blur
 *    - Custom shaders for specialized effects
 *    - Can chain MPS filters with custom kernels
 *
 * 2. Neural Networks:
 *    - MPSCNNConvolution for standard layers
 *    - Custom kernels for novel architectures
 *    - Use MPS for training, custom for inference
 *
 * 3. Matrix Operations:
 *    - MPSMatrixMultiplication for large matrices
 *    - Custom GEMM for specific sizes
 *    - Combine with MPS for hybrid approaches
 *
 * 4. Performance Tips:
 *    - Use MPS when available (it's highly optimized)
 *    - Custom shaders for unique operations
 *    - Profile both approaches on target hardware
 *    - Use half precision where possible
 *    - Leverage threadgroup memory
 */
