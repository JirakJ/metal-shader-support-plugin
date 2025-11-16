//
// metal4_raytracing.metal
// Metal 4 Raytracing Features (iOS 18+, macOS 15+, visionOS 2+)
//
// Demonstrates:
// - Hardware-accelerated raytracing
// - Intersection queries
// - Custom intersection functions
// - Ray payload and attributes
//

#include <metal_stdlib>
#include <metal_raytracing>
using namespace metal;
using namespace raytracing;

// ============================================================================
// RAY STRUCTURES
// ============================================================================

struct Ray {
    packed_float3 origin;
    float minDistance;
    packed_float3 direction;
    float maxDistance;
};

struct RayPayload {
    float3 color;
    float3 attenuation;
    uint bounceCount;
    bool hit;
};

struct Intersection {
    float distance;
    uint primitiveIndex;
    uint geometryIndex;
    float2 barycentric;
};

// ============================================================================
// SCENE STRUCTURES
// ============================================================================

struct Material {
    float3 albedo;
    float metallic;
    float roughness;
    float3 emission;
};

struct Sphere {
    float3 center;
    float radius;
    uint materialIndex;
};

struct Triangle {
    float3 v0, v1, v2;
    float3 n0, n1, n2;
    uint materialIndex;
};

// ============================================================================
// RAYTRACING KERNEL - PRIMARY RAYS
// ============================================================================

kernel void raytracingPrimaryKernel(
    texture2d<float, access::write> outputTexture [[texture(0)]],
    constant float4x4& invViewProjection [[buffer(0)]],
    constant float3& cameraPosition [[buffer(1)]],
    instance_acceleration_structure accelerationStructure [[buffer(2)]],
    constant Material* materials [[buffer(3)]],
    constant uint& sampleCount [[buffer(4)]],
    uint2 tid [[thread_position_in_grid]]
) {
    if (tid.x >= outputTexture.get_width() || tid.y >= outputTexture.get_height()) {
        return;
    }
    
    // Calculate normalized device coordinates
    float2 uv = (float2(tid) + 0.5) / float2(outputTexture.get_width(), outputTexture.get_height());
    uv = uv * 2.0 - 1.0;
    uv.y = -uv.y;
    
    // Create ray
    float4 target = invViewProjection * float4(uv, 1.0, 1.0);
    target /= target.w;
    
    Ray ray;
    ray.origin = cameraPosition;
    ray.direction = normalize(target.xyz - cameraPosition);
    ray.minDistance = 0.001;
    ray.maxDistance = 1000.0;
    
    // Initialize payload
    RayPayload payload;
    payload.color = float3(0.0);
    payload.attenuation = float3(1.0);
    payload.bounceCount = 0;
    payload.hit = false;
    
    // Trace ray
    intersector<triangle_data, instancing> intersector;
    
    typename intersector<triangle_data, instancing>::result_type intersection;
    intersection = intersector.intersect(
        ray,
        accelerationStructure,
        RAY_MASK_ALL
    );
    
    if (intersection.type != intersection_type::none) {
        payload.hit = true;
        
        // Get intersection data
        uint primitiveIndex = intersection.primitive_id;
        uint instanceId = intersection.instance_id;
        float2 barycentric = intersection.triangle_barycentric_coord;
        
        // Simple shading
        float3 normal = float3(0, 1, 0); // Simplified
        float3 lightDir = normalize(float3(1, 1, 1));
        float diffuse = max(dot(normal, lightDir), 0.0);
        
        payload.color = float3(1.0) * diffuse + float3(0.1); // Ambient
    } else {
        // Sky color
        float t = 0.5 * (ray.direction.y + 1.0);
        payload.color = mix(float3(1.0), float3(0.5, 0.7, 1.0), t);
    }
    
    outputTexture.write(float4(payload.color, 1.0), tid);
}

// ============================================================================
// RAYTRACING KERNEL - PATH TRACING
// ============================================================================

kernel void pathTracingKernel(
    texture2d<float, access::write> outputTexture [[texture(0)]],
    texture2d<float, access::read> accumulationTexture [[texture(1)]],
    constant float4x4& invViewProjection [[buffer(0)]],
    constant float3& cameraPosition [[buffer(1)]],
    instance_acceleration_structure accelerationStructure [[buffer(2)]],
    constant Material* materials [[buffer(3)]],
    constant uint& frameIndex [[buffer(4)]],
    constant uint& maxBounces [[buffer(5)]],
    uint2 tid [[thread_position_in_grid]]
) {
    if (tid.x >= outputTexture.get_width() || tid.y >= outputTexture.get_height()) {
        return;
    }
    
    // Simple random number generator (needs proper implementation)
    uint seed = tid.x + tid.y * outputTexture.get_width() + frameIndex * 12345;
    
    float3 color = float3(0.0);
    float3 throughput = float3(1.0);
    
    // Calculate ray
    float2 uv = (float2(tid) + 0.5) / float2(outputTexture.get_width(), outputTexture.get_height());
    uv = uv * 2.0 - 1.0;
    uv.y = -uv.y;
    
    float4 target = invViewProjection * float4(uv, 1.0, 1.0);
    target /= target.w;
    
    float3 rayOrigin = cameraPosition;
    float3 rayDirection = normalize(target.xyz - cameraPosition);
    
    // Path tracing loop
    for (uint bounce = 0; bounce < maxBounces; bounce++) {
        Ray ray;
        ray.origin = rayOrigin;
        ray.direction = rayDirection;
        ray.minDistance = 0.001;
        ray.maxDistance = 1000.0;
        
        intersector<triangle_data, instancing> intersector;
        auto intersection = intersector.intersect(ray, accelerationStructure, RAY_MASK_ALL);
        
        if (intersection.type == intersection_type::none) {
            // Hit sky
            float t = 0.5 * (rayDirection.y + 1.0);
            color += throughput * mix(float3(1.0), float3(0.5, 0.7, 1.0), t);
            break;
        }
        
        // Get material
        Material mat = materials[0]; // Simplified
        
        // Add emission
        color += throughput * mat.emission;
        
        // Russian roulette termination
        if (bounce > 3) {
            float p = max(throughput.x, max(throughput.y, throughput.z));
            if (float(seed) / 4294967295.0 > p) break;
            throughput /= p;
        }
        
        // Update for next bounce (simplified - needs proper BRDF sampling)
        float3 hitPoint = get_world_ray_origin() + get_world_ray_direction() * intersection.distance;
        rayOrigin = hitPoint;
        rayDirection = float3(0, 1, 0); // Simplified normal
        throughput *= mat.albedo;
    }
    
    // Accumulate with previous frames
    float3 accumulated = accumulationTexture.read(tid).rgb;
    float t = 1.0 / float(frameIndex + 1);
    color = mix(accumulated, color, t);
    
    outputTexture.write(float4(color, 1.0), tid);
}

// ============================================================================
// CUSTOM INTERSECTION - SPHERE
// ============================================================================

[[intersection(bounding_box, triangle_data, instancing)]]
bool sphereIntersection(
    ray_data float3 origin [[ray_origin]],
    ray_data float3 direction [[ray_direction]],
    float minDistance [[min_distance]],
    float maxDistance [[max_distance]],
    constant Sphere& sphere [[user_data]],
    uint primitiveIndex [[primitive_id]]
) {
    float3 oc = origin - sphere.center;
    float a = dot(direction, direction);
    float b = 2.0 * dot(oc, direction);
    float c = dot(oc, oc) - sphere.radius * sphere.radius;
    float discriminant = b * b - 4 * a * c;
    
    if (discriminant < 0) {
        return false;
    }
    
    float t = (-b - sqrt(discriminant)) / (2.0 * a);
    
    if (t < minDistance || t > maxDistance) {
        return false;
    }
    
    // Accept hit
    accept_hit_and_end_search();
    return true;
}

// ============================================================================
// INTERSECTION QUERY - ASYNC
// ============================================================================

kernel void asyncIntersectionKernel(
    texture2d<float, access::write> outputTexture [[texture(0)]],
    instance_acceleration_structure accelerationStructure [[buffer(0)]],
    constant float3& rayOrigin [[buffer(1)]],
    constant float3& rayDirection [[buffer(2)]],
    uint2 tid [[thread_position_in_grid]]
) {
    if (tid.x >= outputTexture.get_width() || tid.y >= outputTexture.get_height()) {
        return;
    }
    
    Ray ray;
    ray.origin = rayOrigin;
    ray.direction = rayDirection;
    ray.minDistance = 0.0;
    ray.maxDistance = 1000.0;
    
    // Create intersection query
    intersection_query<instancing> query;
    query.reset(ray, accelerationStructure, RAY_MASK_ALL);
    
    float3 color = float3(0.0);
    bool foundIntersection = false;
    
    // Iterate through all intersections
    while (query.next()) {
        if (query.get_candidate_intersection_type() == intersection_type::triangle) {
            // Process triangle intersection
            float distance = query.get_candidate_triangle_distance();
            
            if (distance > 0.0) {
                foundIntersection = true;
                color = float3(1.0, 0.0, 0.0); // Red for hit
                query.commit_triangle_intersection();
                break;
            }
        }
    }
    
    if (!foundIntersection) {
        color = float3(0.0, 0.0, 1.0); // Blue for miss
    }
    
    outputTexture.write(float4(color, 1.0), tid);
}

// ============================================================================
// VISIBILITY RAYS - SHADOWS
// ============================================================================

kernel void shadowRayKernel(
    texture2d<float, access::read_write> colorTexture [[texture(0)]],
    instance_acceleration_structure accelerationStructure [[buffer(0)]],
    constant float3* hitPoints [[buffer(1)]],
    constant float3& lightPosition [[buffer(2)]],
    uint2 tid [[thread_position_in_grid]]
) {
    if (tid.x >= colorTexture.get_width() || tid.y >= colorTexture.get_height()) {
        return;
    }
    
    uint index = tid.y * colorTexture.get_width() + tid.x;
    float3 hitPoint = hitPoints[index];
    
    // Create shadow ray
    Ray shadowRay;
    shadowRay.origin = hitPoint + float3(0, 0.001, 0); // Offset to avoid self-intersection
    shadowRay.direction = normalize(lightPosition - hitPoint);
    shadowRay.minDistance = 0.0;
    shadowRay.maxDistance = length(lightPosition - hitPoint);
    
    // Trace shadow ray (any hit is enough)
    intersector<triangle_data, instancing> intersector;
    auto intersection = intersector.intersect(shadowRay, accelerationStructure, RAY_MASK_ALL);
    
    float4 color = colorTexture.read(tid);
    
    if (intersection.type != intersection_type::none) {
        // In shadow - darken
        color.rgb *= 0.3;
    }
    
    colorTexture.write(color, tid);
}

// ============================================================================
// AMBIENT OCCLUSION
// ============================================================================

kernel void ambientOcclusionKernel(
    texture2d<float, access::write> aoTexture [[texture(0)]],
    instance_acceleration_structure accelerationStructure [[buffer(0)]],
    constant float3* positions [[buffer(1)]],
    constant float3* normals [[buffer(2)]],
    constant uint& sampleCount [[buffer(3)]],
    uint2 tid [[thread_position_in_grid]]
) {
    if (tid.x >= aoTexture.get_width() || tid.y >= aoTexture.get_height()) {
        return;
    }
    
    uint index = tid.y * aoTexture.get_width() + tid.x;
    float3 position = positions[index];
    float3 normal = normals[index];
    
    float occlusion = 0.0;
    intersector<triangle_data, instancing> intersector;
    
    // Sample hemisphere (simplified)
    for (uint i = 0; i < sampleCount; i++) {
        // Need proper hemisphere sampling
        float3 direction = normal; // Simplified
        
        Ray aoRay;
        aoRay.origin = position + normal * 0.001;
        aoRay.direction = direction;
        aoRay.minDistance = 0.0;
        aoRay.maxDistance = 1.0; // AO radius
        
        auto intersection = intersector.intersect(aoRay, accelerationStructure, RAY_MASK_ALL);
        
        if (intersection.type != intersection_type::none) {
            occlusion += 1.0;
        }
    }
    
    occlusion /= float(sampleCount);
    float ao = 1.0 - occlusion;
    
    aoTexture.write(float4(ao), tid);
}
