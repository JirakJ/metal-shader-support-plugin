//
//  MetalRenderer.mm
//  Metal Shader Integration Example
//
//  Demonstrates Metal shader usage from Objective-C++
//

#import <Metal/Metal.h>
#import <MetalKit/MetalKit.h>
#import <simd/simd.h>

// C++ includes
#include <vector>
#include <memory>

// ============================================================================
// SHADER STRUCTURES (must match Metal shader definitions)
// ============================================================================

struct Vertex {
    simd::float4 position;
    simd::float2 texCoord;
    simd::float3 normal;
    simd::float4 color;
};

struct Uniforms {
    simd::float4x4 modelMatrix;
    simd::float4x4 viewMatrix;
    simd::float4x4 projectionMatrix;
    simd::float3x3 normalMatrix;
    simd::float3 cameraPosition;
    float time;
};

struct Light {
    simd::float3 position;
    simd::float3 color;
    float intensity;
    float radius;
};

// ============================================================================
// METAL RENDERER CLASS
// ============================================================================

@interface MetalRenderer : NSObject

@property (nonatomic, strong) id<MTLDevice> device;
@property (nonatomic, strong) id<MTLCommandQueue> commandQueue;
@property (nonatomic, strong) id<MTLLibrary> shaderLibrary;
@property (nonatomic, strong) id<MTLRenderPipelineState> renderPipeline;
@property (nonatomic, strong) id<MTLComputePipelineState> computePipeline;
@property (nonatomic, strong) id<MTLDepthStencilState> depthState;

@property (nonatomic, strong) id<MTLBuffer> vertexBuffer;
@property (nonatomic, strong) id<MTLBuffer> uniformBuffer;
@property (nonatomic, strong) id<MTLBuffer> lightBuffer;

@property (nonatomic, strong) id<MTLTexture> colorTexture;
@property (nonatomic, strong) id<MTLTexture> normalTexture;
@property (nonatomic, strong) id<MTLTexture> depthTexture;

@property (nonatomic, assign) float time;

- (instancetype)initWithDevice:(id<MTLDevice>)device;
- (void)setupPipeline;
- (void)setupBuffers;
- (void)setupTextures:(CGSize)size;
- (void)renderFrame:(id<MTLCommandBuffer>)commandBuffer
         drawable:(id<CAMetalDrawable>)drawable;
- (void)runComputeShader:(id<MTLCommandBuffer>)commandBuffer;

@end

@implementation MetalRenderer

// ============================================================================
// INITIALIZATION
// ============================================================================

- (instancetype)initWithDevice:(id<MTLDevice>)device {
    self = [super init];
    if (self) {
        _device = device;
        _commandQueue = [device newCommandQueue];
        _time = 0.0f;
        
        [self setupPipeline];
        [self setupBuffers];
        [self setupTextures:CGSizeMake(1024, 1024)];
    }
    return self;
}

// ============================================================================
// PIPELINE SETUP
// ============================================================================

- (void)setupPipeline {
    NSError *error = nil;
    
    // Load shader library
    NSString *shaderPath = [[NSBundle mainBundle] pathForResource:@"comprehensive_demo"
                                                           ofType:@"metal"];
    NSString *shaderSource = [NSString stringWithContentsOfFile:shaderPath
                                                      encoding:NSUTF8StringEncoding
                                                         error:&error];
    
    if (error) {
        NSLog(@"Error loading shader: %@", error);
        return;
    }
    
    MTLCompileOptions *options = [[MTLCompileOptions alloc] init];
    options.languageVersion = MTLLanguageVersion3_0;
    
    _shaderLibrary = [_device newLibraryWithSource:shaderSource
                                           options:options
                                             error:&error];
    
    if (error) {
        NSLog(@"Error compiling shaders: %@", error);
        return;
    }
    
    // Create render pipeline
    id<MTLFunction> vertexFunction = [_shaderLibrary newFunctionWithName:@"basicVertexShader"];
    id<MTLFunction> fragmentFunction = [_shaderLibrary newFunctionWithName:@"texturedFragmentShader"];
    
    MTLRenderPipelineDescriptor *pipelineDescriptor = [[MTLRenderPipelineDescriptor alloc] init];
    pipelineDescriptor.vertexFunction = vertexFunction;
    pipelineDescriptor.fragmentFunction = fragmentFunction;
    pipelineDescriptor.colorAttachments[0].pixelFormat = MTLPixelFormatBGRA8Unorm;
    pipelineDescriptor.depthAttachmentPixelFormat = MTLPixelFormatDepth32Float;
    
    _renderPipeline = [_device newRenderPipelineStateWithDescriptor:pipelineDescriptor
                                                             error:&error];
    
    if (error) {
        NSLog(@"Error creating pipeline: %@", error);
        return;
    }
    
    // Create compute pipeline
    id<MTLFunction> computeFunction = [_shaderLibrary newFunctionWithName:@"blurKernel"];
    _computePipeline = [_device newComputePipelineStateWithFunction:computeFunction
                                                              error:&error];
    
    // Create depth stencil state
    MTLDepthStencilDescriptor *depthDescriptor = [[MTLDepthStencilDescriptor alloc] init];
    depthDescriptor.depthCompareFunction = MTLCompareFunctionLess;
    depthDescriptor.depthWriteEnabled = YES;
    _depthState = [_device newDepthStencilStateWithDescriptor:depthDescriptor];
}

// ============================================================================
// BUFFER SETUP
// ============================================================================

- (void)setupBuffers {
    // Create vertex buffer
    std::vector<Vertex> vertices = {
        // Triangle vertices
        {{-0.5f, -0.5f, 0.0f, 1.0f}, {0.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {1.0f, 0.0f, 0.0f, 1.0f}},
        {{ 0.5f, -0.5f, 0.0f, 1.0f}, {1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 1.0f, 0.0f, 1.0f}},
        {{ 0.0f,  0.5f, 0.0f, 1.0f}, {0.5f, 1.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, 1.0f, 1.0f}},
    };
    
    _vertexBuffer = [_device newBufferWithBytes:vertices.data()
                                         length:vertices.size() * sizeof(Vertex)
                                        options:MTLResourceStorageModeShared];
    
    // Create uniform buffer
    _uniformBuffer = [_device newBufferWithLength:sizeof(Uniforms)
                                          options:MTLResourceStorageModeShared];
    
    // Create light buffer
    std::vector<Light> lights = {
        {{1.0f, 2.0f, 0.0f}, {1.0f, 1.0f, 1.0f}, 1.0f, 10.0f},
        {{-1.0f, 1.0f, 1.0f}, {0.5f, 0.5f, 1.0f}, 0.5f, 5.0f},
    };
    
    _lightBuffer = [_device newBufferWithBytes:lights.data()
                                        length:lights.size() * sizeof(Light)
                                       options:MTLResourceStorageModeShared];
}

// ============================================================================
// TEXTURE SETUP
// ============================================================================

- (void)setupTextures:(CGSize)size {
    MTLTextureDescriptor *descriptor = [MTLTextureDescriptor
        texture2DDescriptorWithPixelFormat:MTLPixelFormatRGBA8Unorm
                                     width:size.width
                                    height:size.height
                                 mipmapped:NO];
    
    descriptor.usage = MTLTextureUsageShaderRead | MTLTextureUsageShaderWrite;
    descriptor.storageMode = MTLStorageModePrivate;
    
    _colorTexture = [_device newTextureWithDescriptor:descriptor];
    _normalTexture = [_device newTextureWithDescriptor:descriptor];
    
    // Create depth texture
    MTLTextureDescriptor *depthDescriptor = [MTLTextureDescriptor
        texture2DDescriptorWithPixelFormat:MTLPixelFormatDepth32Float
                                     width:size.width
                                    height:size.height
                                 mipmapped:NO];
    
    depthDescriptor.usage = MTLTextureUsageRenderTarget;
    depthDescriptor.storageMode = MTLStorageModePrivate;
    
    _depthTexture = [_device newTextureWithDescriptor:depthDescriptor];
}

// ============================================================================
// RENDER FRAME
// ============================================================================

- (void)renderFrame:(id<MTLCommandBuffer>)commandBuffer
         drawable:(id<CAMetalDrawable>)drawable {
    
    // Update uniforms
    _time += 0.016f; // Assume 60 FPS
    
    Uniforms *uniforms = (Uniforms *)[_uniformBuffer contents];
    
    // Model matrix (identity)
    uniforms->modelMatrix = matrix_identity_float4x4;
    
    // View matrix (camera at (0, 0, 3) looking at origin)
    simd::float3 eye = {0.0f, 0.0f, 3.0f};
    simd::float3 center = {0.0f, 0.0f, 0.0f};
    simd::float3 up = {0.0f, 1.0f, 0.0f};
    uniforms->viewMatrix = matrix_look_at_left_hand(eye, center, up);
    
    // Projection matrix (perspective)
    float aspect = drawable.texture.width / (float)drawable.texture.height;
    uniforms->projectionMatrix = matrix_perspective_left_hand(M_PI / 4.0f, aspect, 0.1f, 100.0f);
    
    // Normal matrix
    uniforms->normalMatrix = matrix_float3x3(uniforms->modelMatrix);
    
    // Camera position
    uniforms->cameraPosition = eye;
    
    // Time
    uniforms->time = _time;
    
    // Create render pass
    MTLRenderPassDescriptor *renderPassDescriptor = [MTLRenderPassDescriptor renderPassDescriptor];
    renderPassDescriptor.colorAttachments[0].texture = drawable.texture;
    renderPassDescriptor.colorAttachments[0].loadAction = MTLLoadActionClear;
    renderPassDescriptor.colorAttachments[0].clearColor = MTLClearColorMake(0.1, 0.1, 0.2, 1.0);
    renderPassDescriptor.colorAttachments[0].storeAction = MTLStoreActionStore;
    
    renderPassDescriptor.depthAttachment.texture = _depthTexture;
    renderPassDescriptor.depthAttachment.loadAction = MTLLoadActionClear;
    renderPassDescriptor.depthAttachment.clearDepth = 1.0;
    renderPassDescriptor.depthAttachment.storeAction = MTLStoreActionDontCare;
    
    // Encode render commands
    id<MTLRenderCommandEncoder> renderEncoder = [commandBuffer renderCommandEncoderWithDescriptor:renderPassDescriptor];
    
    [renderEncoder setRenderPipelineState:_renderPipeline];
    [renderEncoder setDepthStencilState:_depthState];
    
    [renderEncoder setVertexBuffer:_vertexBuffer offset:0 atIndex:0];
    [renderEncoder setVertexBuffer:_uniformBuffer offset:0 atIndex:0];
    
    [renderEncoder setFragmentTexture:_colorTexture atIndex:0];
    [renderEncoder setFragmentTexture:_normalTexture atIndex:1];
    [renderEncoder setFragmentBuffer:_uniformBuffer offset:0 atIndex:0];
    [renderEncoder setFragmentBuffer:_lightBuffer offset:0 atIndex:1];
    
    uint32_t lightCount = 2;
    [renderEncoder setFragmentBytes:&lightCount length:sizeof(uint32_t) atIndex:2];
    
    [renderEncoder drawPrimitives:MTLPrimitiveTypeTriangle
                      vertexStart:0
                      vertexCount:3];
    
    [renderEncoder endEncoding];
}

// ============================================================================
// COMPUTE SHADER
// ============================================================================

- (void)runComputeShader:(id<MTLCommandBuffer>)commandBuffer {
    id<MTLComputeCommandEncoder> computeEncoder = [commandBuffer computeCommandEncoder];
    
    [computeEncoder setComputePipelineState:_computePipeline];
    [computeEncoder setTexture:_colorTexture atIndex:0];
    [computeEncoder setTexture:_normalTexture atIndex:1];
    
    float blurRadius = 5.0f;
    [computeEncoder setBytes:&blurRadius length:sizeof(float) atIndex:0];
    
    MTLSize threadgroupSize = MTLSizeMake(16, 16, 1);
    MTLSize threadgroups = MTLSizeMake(
        (_colorTexture.width + threadgroupSize.width - 1) / threadgroupSize.width,
        (_colorTexture.height + threadgroupSize.height - 1) / threadgroupSize.height,
        1
    );
    
    [computeEncoder dispatchThreadgroups:threadgroups
                   threadsPerThreadgroup:threadgroupSize];
    
    [computeEncoder endEncoding];
}

// ============================================================================
// HELPER FUNCTIONS (C++)
// ============================================================================

simd::float4x4 matrix_identity_float4x4 {
    return simd::float4x4{
        {1, 0, 0, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {0, 0, 0, 1}
    };
}

simd::float4x4 matrix_look_at_left_hand(simd::float3 eye, simd::float3 center, simd::float3 up) {
    simd::float3 f = simd::normalize(center - eye);
    simd::float3 s = simd::normalize(simd::cross(f, up));
    simd::float3 u = simd::cross(s, f);
    
    return simd::float4x4{
        {s.x, u.x, -f.x, 0},
        {s.y, u.y, -f.y, 0},
        {s.z, u.z, -f.z, 0},
        {-simd::dot(s, eye), -simd::dot(u, eye), simd::dot(f, eye), 1}
    };
}

simd::float4x4 matrix_perspective_left_hand(float fovY, float aspect, float nearZ, float farZ) {
    float ys = 1.0f / tanf(fovY * 0.5f);
    float xs = ys / aspect;
    float zs = farZ / (farZ - nearZ);
    
    return simd::float4x4{
        {xs, 0, 0, 0},
        {0, ys, 0, 0},
        {0, 0, zs, 1},
        {0, 0, -nearZ * zs, 0}
    };
}

simd::float3x3 matrix_float3x3(simd::float4x4 m) {
    return simd::float3x3{
        {m.columns[0].x, m.columns[0].y, m.columns[0].z},
        {m.columns[1].x, m.columns[1].y, m.columns[1].z},
        {m.columns[2].x, m.columns[2].y, m.columns[2].z}
    };
}

@end

// ============================================================================
// USAGE EXAMPLE
// ============================================================================

/*
 
 // In your view controller:
 
 - (void)viewDidLoad {
     [super viewDidLoad];
     
     // Setup Metal view
     MTKView *metalView = (MTKView *)self.view;
     metalView.device = MTLCreateSystemDefaultDevice();
     
     // Create renderer
     self.renderer = [[MetalRenderer alloc] initWithDevice:metalView.device];
     
     // Setup delegate
     metalView.delegate = self;
 }
 
 - (void)drawInMTKView:(MTKView *)view {
     id<MTLCommandBuffer> commandBuffer = [self.renderer.commandQueue commandBuffer];
     
     // Run compute shader first (optional)
     [self.renderer runComputeShader:commandBuffer];
     
     // Render frame
     [self.renderer renderFrame:commandBuffer drawable:view.currentDrawable];
     
     [commandBuffer presentDrawable:view.currentDrawable];
     [commandBuffer commit];
 }
 
*/
