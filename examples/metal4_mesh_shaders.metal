//
// metal4_mesh_shaders.metal
// Metal 4 Mesh Shading Pipeline (iOS 18+, macOS 15+, visionOS 2+)
//
// Demonstrates:
// - Object shaders (amplification/task shaders)
// - Mesh shaders (geometry generation on GPU)
// - Dynamic mesh generation
// - LOD selection on GPU
//

#include <metal_stdlib>
using namespace metal;

// ============================================================================
// MESH SHADER STRUCTURES
// ============================================================================

struct MeshVertex {
    float4 position [[position]];
    float3 normal;
    float2 texCoord;
    float4 color;
};

struct MeshPrimitive {
    float4 color;
    uint materialId;
};

struct ObjectPayload {
    uint lodLevel;
    float4 boundingSphere;
    uint visibleMeshlets;
    uint meshletIndices[32];
};

struct Meshlet {
    float3 center;
    float radius;
    uint vertexOffset;
    uint vertexCount;
    uint primitiveOffset;
    uint primitiveCount;
};

struct SceneData {
    float4x4 viewProjection;
    float3 cameraPosition;
    float time;
    float4 frustumPlanes[6];
};

// ============================================================================
// OBJECT SHADER (Task/Amplification Shader)
// ============================================================================

using MeshGridProps = mesh_grid_properties;

[[object, max_total_threadgroups_per_mesh_grid(32)]]
void objectShader(
    object_data ObjectPayload& payload [[payload]],
    constant SceneData& sceneData [[buffer(0)]],
    constant Meshlet* meshlets [[buffer(1)]],
    constant uint& meshletCount [[buffer(2)]],
    uint objectId [[object_id]],
    uint threadId [[thread_position_in_grid]],
    mesh_grid_properties meshGridProps
) {
    // Frustum culling and LOD selection
    if (threadId == 0) {
        payload.visibleMeshlets = 0;
        payload.lodLevel = 0;
        
        // Calculate LOD based on distance
        float3 objectCenter = float3(0.0); // Get from object data
        float distance = length(sceneData.cameraPosition - objectCenter);
        
        if (distance > 100.0) {
            payload.lodLevel = 2; // Low detail
        } else if (distance > 50.0) {
            payload.lodLevel = 1; // Medium detail
        } else {
            payload.lodLevel = 0; // High detail
        }
    }
    
    threadgroup_barrier(mem_flags::mem_device);
    
    // Process meshlets in parallel
    if (threadId < meshletCount) {
        Meshlet meshlet = meshlets[threadId];
        
        // Frustum culling
        bool visible = true;
        for (uint i = 0; i < 6; i++) {
            float4 plane = sceneData.frustumPlanes[i];
            float distance = dot(float4(meshlet.center, 1.0), plane);
            if (distance < -meshlet.radius) {
                visible = false;
                break;
            }
        }
        
        // Add to visible list
        if (visible) {
            uint index = atomic_fetch_add_explicit(
                (device atomic_uint*)&payload.visibleMeshlets,
                1,
                memory_order_relaxed
            );
            
            if (index < 32) {
                payload.meshletIndices[index] = threadId;
            }
        }
    }
    
    threadgroup_barrier(mem_flags::mem_device);
    
    // Dispatch mesh shader for visible meshlets
    if (threadId == 0) {
        uint meshletsToDraw = min(payload.visibleMeshlets, 32u);
        meshGridProps.set_threadgroups_per_grid(uint3(meshletsToDraw, 1, 1));
    }
}

// ============================================================================
// MESH SHADER (Geometry Generation)
// ============================================================================

using MeshOutput = mesh<
    MeshVertex,          // Vertex type
    MeshPrimitive,       // Primitive type
    64,                  // Max vertices
    126,                 // Max primitives (triangles)
    topology::triangle   // Primitive topology
>;

[[mesh, max_total_threads_per_threadgroup(128)]]
void meshShader(
    MeshOutput output,
    object_data ObjectPayload& payload [[payload]],
    constant SceneData& sceneData [[buffer(0)]],
    constant Meshlet* meshlets [[buffer(1)]],
    constant float3* positions [[buffer(2)]],
    constant float3* normals [[buffer(3)]],
    constant float2* texCoords [[buffer(4)]],
    constant uint* indices [[buffer(5)]],
    uint meshletId [[threadgroup_position_in_grid]],
    uint threadId [[thread_position_in_threadgroup]]
) {
    // Get meshlet for this thread group
    if (meshletId >= payload.visibleMeshlets) {
        return;
    }
    
    uint meshletIndex = payload.meshletIndices[meshletId];
    Meshlet meshlet = meshlets[meshletIndex];
    
    // Set output counts
    if (threadId == 0) {
        output.set_vertex_count(meshlet.vertexCount);
        output.set_primitive_count(meshlet.primitiveCount);
    }
    
    // Generate vertices in parallel
    if (threadId < meshlet.vertexCount) {
        uint vertexIndex = meshlet.vertexOffset + threadId;
        
        float3 position = positions[vertexIndex];
        float3 normal = normals[vertexIndex];
        float2 texCoord = texCoords[vertexIndex];
        
        // Transform vertex
        float4 worldPos = float4(position, 1.0);
        float4 clipPos = sceneData.viewProjection * worldPos;
        
        // Write vertex output
        MeshVertex vertex;
        vertex.position = clipPos;
        vertex.normal = normal;
        vertex.texCoord = texCoord;
        vertex.color = float4(1.0);
        
        output.set_vertex(threadId, vertex);
    }
    
    // Generate primitives (triangles) in parallel
    if (threadId < meshlet.primitiveCount) {
        uint primitiveIndex = meshlet.primitiveOffset + threadId;
        
        // Read triangle indices (3 per primitive)
        uint i0 = indices[primitiveIndex * 3 + 0];
        uint i1 = indices[primitiveIndex * 3 + 1];
        uint i2 = indices[primitiveIndex * 3 + 2];
        
        // Set triangle indices
        output.set_index(threadId * 3 + 0, i0);
        output.set_index(threadId * 3 + 1, i1);
        output.set_index(threadId * 3 + 2, i2);
        
        // Set per-primitive data
        MeshPrimitive primitive;
        primitive.color = float4(1.0, 1.0, 1.0, 1.0);
        primitive.materialId = 0;
        
        output.set_primitive(threadId, primitive);
    }
}

// ============================================================================
// PROCEDURAL MESH GENERATION
// ============================================================================

[[mesh, max_total_threads_per_threadgroup(64)]]
void proceduralSphereShader(
    MeshOutput output,
    constant SceneData& sceneData [[buffer(0)]],
    constant float& radius [[buffer(1)]],
    constant uint& subdivisions [[buffer(2)]],
    uint threadgroupId [[threadgroup_position_in_grid]],
    uint threadId [[thread_position_in_threadgroup]]
) {
    // Generate sphere vertices procedurally
    uint latSegments = subdivisions;
    uint lonSegments = subdivisions * 2;
    uint vertexCount = (latSegments + 1) * (lonSegments + 1);
    uint triangleCount = latSegments * lonSegments * 2;
    
    if (threadId == 0) {
        output.set_vertex_count(min(vertexCount, 64u));
        output.set_primitive_count(min(triangleCount, 126u));
    }
    
    // Generate vertices
    if (threadId < vertexCount && threadId < 64) {
        uint lat = threadId / (lonSegments + 1);
        uint lon = threadId % (lonSegments + 1);
        
        float theta = float(lat) / float(latSegments) * M_PI_F;
        float phi = float(lon) / float(lonSegments) * 2.0 * M_PI_F;
        
        float3 position;
        position.x = radius * sin(theta) * cos(phi);
        position.y = radius * cos(theta);
        position.z = radius * sin(theta) * sin(phi);
        
        float3 normal = normalize(position);
        float2 texCoord = float2(float(lon) / lonSegments, float(lat) / latSegments);
        
        // Animate
        position += normal * sin(sceneData.time + phi) * 0.1;
        
        MeshVertex vertex;
        vertex.position = sceneData.viewProjection * float4(position, 1.0);
        vertex.normal = normal;
        vertex.texCoord = texCoord;
        vertex.color = float4(normal * 0.5 + 0.5, 1.0);
        
        output.set_vertex(threadId, vertex);
    }
    
    // Generate triangles
    uint triangleId = threadId;
    if (triangleId < triangleCount && triangleId < 126) {
        uint quadId = triangleId / 2;
        uint lat = quadId / lonSegments;
        uint lon = quadId % lonSegments;
        
        uint v0 = lat * (lonSegments + 1) + lon;
        uint v1 = v0 + 1;
        uint v2 = v0 + (lonSegments + 1);
        uint v3 = v2 + 1;
        
        if (triangleId % 2 == 0) {
            // First triangle of quad
            output.set_index(triangleId * 3 + 0, v0);
            output.set_index(triangleId * 3 + 1, v2);
            output.set_index(triangleId * 3 + 2, v1);
        } else {
            // Second triangle of quad
            output.set_index(triangleId * 3 + 0, v1);
            output.set_index(triangleId * 3 + 1, v2);
            output.set_index(triangleId * 3 + 2, v3);
        }
        
        MeshPrimitive primitive;
        primitive.color = float4(1.0);
        primitive.materialId = 0;
        output.set_primitive(triangleId, primitive);
    }
}

// ============================================================================
// DYNAMIC TESSELLATION MESH SHADER
// ============================================================================

[[mesh, max_total_threads_per_threadgroup(64)]]
void tessellationMeshShader(
    MeshOutput output,
    object_data ObjectPayload& payload [[payload]],
    constant SceneData& sceneData [[buffer(0)]],
    constant float4* controlPoints [[buffer(1)]],
    uint meshletId [[threadgroup_position_in_grid]],
    uint threadId [[thread_position_in_threadgroup]]
) {
    // Tessellation level from payload
    uint tessLevel = (3 - payload.lodLevel) * 8; // Higher LOD = more tessellation
    uint verticesPerSide = tessLevel + 1;
    uint vertexCount = verticesPerSide * verticesPerSide;
    uint quadCount = tessLevel * tessLevel;
    uint triangleCount = quadCount * 2;
    
    if (threadId == 0) {
        output.set_vertex_count(min(vertexCount, 64u));
        output.set_primitive_count(min(triangleCount, 126u));
    }
    
    // Generate tessellated vertices
    if (threadId < vertexCount && threadId < 64) {
        uint u = threadId / verticesPerSide;
        uint v = threadId % verticesPerSide;
        
        float2 uv = float2(u, v) / float(tessLevel);
        
        // Bezier patch evaluation (simplified)
        float3 position = float3(uv.x, 0.0, uv.y);
        
        // Add displacement
        float height = sin(uv.x * M_PI_F * 4.0) * sin(uv.y * M_PI_F * 4.0) * 0.2;
        position.y = height;
        
        float3 normal = float3(0, 1, 0); // Simplified
        
        MeshVertex vertex;
        vertex.position = sceneData.viewProjection * float4(position, 1.0);
        vertex.normal = normal;
        vertex.texCoord = uv;
        vertex.color = float4(height + 0.5, 0.5, 1.0 - height, 1.0);
        
        output.set_vertex(threadId, vertex);
    }
    
    // Generate triangles
    uint triangleId = threadId;
    if (triangleId < triangleCount && triangleId < 126) {
        uint quadId = triangleId / 2;
        uint row = quadId / tessLevel;
        uint col = quadId % tessLevel;
        
        uint v0 = row * verticesPerSide + col;
        uint v1 = v0 + 1;
        uint v2 = v0 + verticesPerSide;
        uint v3 = v2 + 1;
        
        if (triangleId % 2 == 0) {
            output.set_index(triangleId * 3 + 0, v0);
            output.set_index(triangleId * 3 + 1, v2);
            output.set_index(triangleId * 3 + 2, v1);
        } else {
            output.set_index(triangleId * 3 + 0, v1);
            output.set_index(triangleId * 3 + 1, v2);
            output.set_index(triangleId * 3 + 2, v3);
        }
        
        MeshPrimitive primitive;
        primitive.color = float4(1.0);
        primitive.materialId = 0;
        output.set_primitive(triangleId, primitive);
    }
}

// ============================================================================
// FRAGMENT SHADER
// ============================================================================

fragment float4 meshFragmentShader(
    MeshVertex in [[stage_in]],
    primitive_data MeshPrimitive primitive [[primitive_data]],
    bool isFrontFacing [[front_facing]]
) {
    // Simple shading with primitive color
    float3 normal = normalize(in.normal);
    if (!isFrontFacing) {
        normal = -normal;
    }
    
    float3 lightDir = normalize(float3(1, 1, 1));
    float diffuse = max(dot(normal, lightDir), 0.0);
    
    float3 color = in.color.rgb * primitive.color.rgb;
    color = color * diffuse + color * 0.2; // Ambient
    
    return float4(color, 1.0);
}
