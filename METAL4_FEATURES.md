# Metal 4 Features Support (iOS 18+, macOS 15+, visionOS 2+)

## Overview

This plugin now includes **full support for Metal 4** - the latest version of Apple's Metal Shading Language with revolutionary new features.

## 🌟 What's New in Metal 4

### 1. Hardware-Accelerated Raytracing 🚀

Full GPU raytracing support with dedicated hardware acceleration on Apple Silicon (M3+).

#### Features:
- **Acceleration Structures**: Instance and primitive acceleration structures
- **Ray Queries**: Synchronous and asynchronous intersection testing
- **Custom Intersections**: Define custom geometry (spheres, AABBs, etc.)
- **Ray Payloads**: Pass data between ray stages
- **Intersection Attributes**: Barycentric coordinates, normals, etc.

#### Example:
```metal
kernel void raytracingKernel(
    texture2d<float, access::write> output [[texture(0)]],
    instance_acceleration_structure scene [[buffer(0)]],
    uint2 gid [[thread_position_in_grid]]
) {
    Ray ray = createCameraRay(gid);
    
    intersector<triangle_data, instancing> intersector;
    auto hit = intersector.intersect(ray, scene, RAY_MASK_ALL);
    
    if (hit.type != intersection_type::none) {
        // Process hit
        float3 color = shadeHit(hit);
        output.write(float4(color, 1.0), gid);
    }
}
```

### 2. Mesh Shading Pipeline 🎨

Revolutionary new geometry pipeline replacing vertex shaders with programmable mesh generation.

#### Components:

**Object Shader** (Task/Amplification):
- Runs before mesh shader
- Performs culling and LOD selection
- Spawns mesh shader invocations
- Similar to Vulkan task shader or DX12 amplification shader

```metal
[[object, max_total_threadgroups_per_mesh_grid(32)]]
void objectShader(
    object_data Payload& payload [[payload]],
    mesh_grid_properties meshProps
) {
    // Cull invisible meshlets
    // Select LOD level
    // Dispatch visible meshlets
    meshProps.set_threadgroups_per_grid(visibleCount);
}
```

**Mesh Shader**:
- Generates vertices and primitives dynamically
- Full parallel execution
- Perfect for GPU-driven rendering

```metal
[[mesh, max_total_threads_per_threadgroup(128)]]
void meshShader(
    MeshOutput output,
    object_data Payload& payload [[payload]]
) {
    // Generate geometry on GPU
    output.set_vertex_count(64);
    output.set_primitive_count(126);
    
    // Parallel vertex generation
    output.set_vertex(threadId, vertex);
    output.set_index(threadId, index);
}
```

### 3. New Types

#### Raytracing Types:
- `instance_acceleration_structure` - Scene-level acceleration structure
- `primitive_acceleration_structure` - Geometry-level acceleration structure
- `intersection_result` - Ray-geometry intersection result
- `intersection_query` - Async intersection testing
- `ray_data` - Ray payload data

#### Mesh Shader Types:
- `mesh_grid_properties` - Mesh dispatch properties
- `object_data` - Object shader payload
- `primitive_data` - Per-primitive fragment shader data

#### 64-bit Types:
- `long2`, `long3`, `long4` - 64-bit integer vectors
- `ulong2`, `ulong3`, `ulong4` - Unsigned 64-bit vectors
- `double2`, `double3`, `double4` - Double precision vectors

#### Atomic Types:
- `atomic_float` - Atomic floating point (NEW!)
- `atomic_double` - Atomic double precision
- `atomic_bool` - Atomic boolean

### 4. New Built-in Functions

#### Raytracing Functions:
```metal
// Ray tracing
intersection_result trace_ray(ray, acceleration_structure, mask)
bool intersect_ray(ray, acceleration_structure) // Shadow rays

// Intersection control
accept_hit_and_end_search()
ignore_intersection()

// Ray properties
float3 get_world_ray_origin()
float3 get_world_ray_direction()
float get_ray_min_distance()
float get_ray_max_distance()

// Hit properties
float2 get_triangle_barycentric_coord()
uint get_instance_id()
uint get_geometry_id()
uint get_primitive_id()
float4x3 get_world_to_object_transform()
float4x3 get_object_to_world_transform()
```

#### Mesh Shader Functions:
```metal
void set_mesh_outputs(uint vertexCount, uint primitiveCount)
void set_vertex_count(uint count)
void set_primitive_count(uint count)
void set_index(uint index, uint value)
void set_primitive_type(topology type)
```

#### Texture Functions:
```metal
// Sparse textures (NEW!)
sparse_result sparse_sample(texture, sampler, coord)
sparse_result sparse_read(texture, coord)
void sparse_write(texture, value, coord)
```

### 5. New Attributes

#### Raytracing Attributes:
- `[[payload]]` - Ray payload data
- `[[instance_data]]` - Per-instance data
- `[[primitive_data]]` - Per-primitive data (mesh shaders too!)
- `[[ray_origin]]` - Ray origin in intersection function
- `[[ray_direction]]` - Ray direction
- `[[min_distance]]` - Minimum ray distance
- `[[max_distance]]` - Maximum ray distance

#### Mesh Shader Attributes:
- `[[object]]` - Object shader function qualifier
- `[[mesh]]` - Mesh shader function qualifier
- `[[max_total_threads_per_threadgroup]]` - Mesh shader thread limit
- `[[max_total_threadgroups_per_mesh_grid]]` - Object shader dispatch limit
- `[[threadgroup_position_in_grid]]` - Meshlet ID in mesh shader
- `[[object_id]]` - Object ID in object shader

### 6. Qualifiers

```metal
// Mesh shader qualifiers
max_total_threads_per_threadgroup(N)
max_total_threadgroups_per_mesh_grid(N)

// Fragment shader optimizations
early_fragment_tests  // Force early depth test

// Ray tracing address spaces
ray_data              // Ray payload address space
instance_data         // Instance acceleration structure data
primitive_data        // Primitive data
```

## 📚 Example Files

### 1. `metal4_raytracing.metal` (350+ lines)
Comprehensive raytracing examples:
- ✅ Primary ray kernel
- ✅ Path tracing
- ✅ Custom sphere intersection
- ✅ Async intersection queries
- ✅ Shadow rays
- ✅ Ambient occlusion

### 2. `metal4_mesh_shaders.metal` (350+ lines)
Complete mesh shading pipeline:
- ✅ Object shader with culling
- ✅ Mesh shader with meshlets
- ✅ Procedural sphere generation
- ✅ Dynamic tessellation
- ✅ LOD selection

## 🎯 Live Templates

Type these shortcuts + Tab to insert Metal 4 code:

### `raytrace` + Tab
```metal
kernel void raytracingKernel(
    texture2d<float, access::write> outputTexture [[texture(0)]],
    instance_acceleration_structure accelerationStructure [[buffer(0)]],
    constant float3& rayOrigin [[buffer(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    // Full raytracing kernel template
}
```

### `objectshader` + Tab
```metal
[[object, max_total_threadgroups_per_mesh_grid(32)]]
void objectShader(
    object_data Payload& payload [[payload]],
    mesh_grid_properties meshGridProps
) {
    // Object shader template
}
```

### `meshshader` + Tab
```metal
using MeshOutput = mesh<Vertex, Primitive, 64, 126, topology::triangle>;

[[mesh, max_total_threads_per_threadgroup(128)]]
void meshShader(MeshOutput output) {
    // Mesh shader template
}
```

## 💡 Hover Documentation

All Metal 4 keywords and functions have hover tooltips:

- Hover over `object` → See object shader documentation
- Hover over `mesh` → See mesh shader documentation
- Hover over `trace_ray` → See raytracing API docs
- Hover over `[[payload]]` → See ray payload explanation

## 🔧 IDE Features

### Syntax Highlighting
All Metal 4 keywords are properly highlighted:
- `object`, `mesh` - Mesh shader qualifiers
- `instance_acceleration_structure` - Raytracing types
- `trace_ray`, `intersect_ray` - Raytracing functions

### Code Completion
Auto-complete suggestions for:
- Raytracing API functions
- Mesh shader functions
- New types and qualifiers
- Attribute suggestions

### Error Detection
Validates:
- Mesh shader output limits
- Raytracing structure access
- Proper attribute usage

## 🚀 Getting Started

### 1. Check Device Support
```swift
// In your Swift/Objective-C code:
if device.supportsFamily(.apple9) {
    // Metal 4 supported!
    // M3+, A17 Pro+, etc.
}
```

### 2. Create a Metal 4 Shader
- Right-click → New → Metal Shader
- Or use live templates: `raytrace` + Tab

### 3. Explore Examples
Open `examples/metal4_raytracing.metal` or `metal4_mesh_shaders.metal`

### 4. Build Acceleration Structure
```swift
// Build acceleration structure (Swift)
let descriptor = MTLAccelerationStructureDescriptor()
let accelerationStructure = device.makeAccelerationStructure(descriptor: descriptor)
```

## 📋 Keywords Added (150+)

**Mesh Shading**: `object`, `mesh`, `mesh_grid_properties`, `object_data`, `primitive_data`

**Raytracing**: `instance_acceleration_structure`, `primitive_acceleration_structure`, `intersection_result`, `intersection_query`, `ray_data`

**Functions**: `trace_ray`, `intersect_ray`, `accept_hit_and_end_search`, `get_world_ray_origin`, `set_mesh_outputs`, `mesh_grid_barrier`

**64-bit Types**: `long2-4`, `ulong2-4`, `double2-4`

**Atomics**: `atomic_float`, `atomic_double`, `atomic_bool`

## 🎓 Use Cases

### Raytracing Perfect For:
- **Global Illumination** - Realistic lighting with bounced light
- **Reflections** - Perfect mirror and glossy reflections
- **Shadows** - Accurate soft shadows
- **Ambient Occlusion** - Contact shadows
- **Path Tracing** - Photorealistic rendering

### Mesh Shaders Perfect For:
- **GPU-Driven Rendering** - Eliminate CPU bottlenecks
- **Dynamic LOD** - Adaptive detail based on distance
- **Procedural Geometry** - Generate meshes on GPU
- **Vegetation** - Millions of grass blades/trees
- **Particle Systems** - GPU-generated particle geometry

## 🔗 Resources

- [Metal 4 Release Notes](https://developer.apple.com/metal/)
- [Raytracing Programming Guide](https://developer.apple.com/documentation/metal/metal_raytracing)
- [Mesh Shading Guide](https://developer.apple.com/documentation/metal/mesh_shaders)

## ⚡ Performance Tips

### Raytracing:
- Use `intersect_ray()` for shadows (faster than `trace_ray()`)
- Build compact acceleration structures
- Limit ray bounces (3-5 is usually enough)
- Use denoising for lower sample counts

### Mesh Shaders:
- Keep meshlets small (64-126 triangles)
- Do aggressive culling in object shader
- Use threadgroup memory for sharing data
- Balance vertex count vs primitive count

## 🎉 Summary

Metal 4 support includes:
- ✅ **700+ lines** of example code
- ✅ **150+ new keywords** and functions
- ✅ **3 new live templates**
- ✅ **Hover documentation** for all features
- ✅ **Syntax highlighting** 
- ✅ **Code completion**
- ✅ **Error detection**

**Metal 4 is the future of GPU programming on Apple platforms!** 🚀

---

Compatible with: iOS 18+, macOS 15+, visionOS 2+, Apple Silicon M3+, A17 Pro+
