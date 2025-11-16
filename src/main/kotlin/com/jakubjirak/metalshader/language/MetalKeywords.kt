package com.jakubjirak.metalshader.language

object MetalKeywords {
    val KEYWORDS = setOf(
        // Function qualifiers
        "kernel", "vertex", "fragment",
        
        // Metal 4 - Mesh shader qualifiers (iOS 18+, macOS 15+)
        "object", "mesh",
        
        // Control flow
        "if", "else", "for", "while", "do", "switch", "case", "default",
        "break", "continue", "return", "discard",
        
        // Storage
        "struct", "enum", "typedef", "namespace", "using",
        "template", "typename",
        
        // Metal 4 - Visibility and barriers
        "visible", "intersection",
        
        // Other
        "const", "constexpr", "static", "inline", "extern",
        "auto", "true", "false", "sizeof", "alignas", "alignof"
    )
    
    val TYPES = setOf(
        // Scalar types
        "void", "bool", "char", "short", "int", "uint",
        "half", "float", "double",
        "uchar", "ushort", "ulong", "long",
        "int8_t", "int16_t", "int32_t", "int64_t",
        "uint8_t", "uint16_t", "uint32_t", "uint64_t",
        "size_t", "ptrdiff_t",
        
        // Vector types
        "bool2", "bool3", "bool4",
        "char2", "char3", "char4",
        "uchar2", "uchar3", "uchar4",
        "short2", "short3", "short4",
        "ushort2", "ushort3", "ushort4",
        "int2", "int3", "int4",
        "uint2", "uint3", "uint4",
        "half2", "half3", "half4",
        "float2", "float3", "float4",
        
        // Matrix types
        "float2x2", "float2x3", "float2x4",
        "float3x2", "float3x3", "float3x4",
        "float4x2", "float4x3", "float4x4",
        "half2x2", "half2x3", "half2x4",
        "half3x2", "half3x3", "half3x4",
        "half4x2", "half4x3", "half4x4",
        
        // Texture types
        "texture1d", "texture1d_array",
        "texture2d", "texture2d_array", "texture2d_ms", "texture2d_ms_array",
        "texture3d", "texturecube", "texturecube_array",
        "depth2d", "depth2d_array", "depth2d_ms", "depth2d_ms_array",
        "depthcube", "depthcube_array",
        
        // Sampler
        "sampler",
        
        // Buffer types
        "device", "constant", "threadgroup", "threadgroup_imageblock",
        
        // Metal 4 - Raytracing types (iOS 18+, macOS 15+)
        "instance_acceleration_structure",
        "primitive_acceleration_structure",
        "intersection_result",
        "intersection_query",
        "ray_data",
        
        // Metal 4 - Mesh shader types
        "mesh_grid_properties",
        "object_data",
        "primitive_data",
        
        // Atomic types
        "atomic_int", "atomic_uint", "atomic_bool",
        "atomic_float", "atomic_double",
        
        // Metal 4 - 64-bit vector types
        "long2", "long3", "long4",
        "ulong2", "ulong3", "ulong4",
        "double2", "double3", "double4"
    )
    
    val QUALIFIERS = setOf(
        // Address space
        "device", "threadgroup", "constant", "thread",
        
        // Metal 4 - Ray tracing address spaces
        "ray_data", "instance_data", "primitive_data",
        
        // Access qualifiers
        "read", "write", "read_write",
        "sample",
        
        // Function qualifiers
        "inline", "noinline",
        
        // Metal 4 - Mesh shader qualifiers
        "max_total_threads_per_threadgroup",
        "max_total_threadgroups_per_mesh_grid",
        
        // Interpolation
        "center_perspective", "center_no_perspective",
        "centroid_perspective", "centroid_no_perspective",
        "sample_perspective", "sample_no_perspective",
        "flat",
        
        // Metal 4 - Additional qualifiers
        "early_fragment_tests"
    )
    
    val BUILT_IN_FUNCTIONS = setOf(
        // Math functions
        "abs", "acos", "acosh", "asin", "asinh", "atan", "atan2", "atanh",
        "ceil", "clamp", "cos", "cosh", "cross", "degrees", "distance",
        "dot", "exp", "exp2", "faceforward", "floor", "fma", "fmod",
        "fract", "frexp", "length", "log", "log2", "max", "min", "mix",
        "modf", "normalize", "pow", "radians", "reflect", "refract",
        "round", "rsqrt", "saturate", "sign", "sin", "sinh", "smoothstep",
        "sqrt", "step", "tan", "tanh", "trunc",
        
        // Geometric functions
        "determinant", "transpose",
        
        // Relational functions
        "all", "any", "select",
        
        // Texture functions
        "sample", "read", "write", "gather",
        "get_width", "get_height", "get_depth",
        "get_num_samples", "get_num_mip_levels", "get_array_size",
        
        // Atomic functions
        "atomic_load", "atomic_store", "atomic_exchange",
        "atomic_compare_exchange_weak", "atomic_fetch_add",
        "atomic_fetch_sub", "atomic_fetch_and", "atomic_fetch_or",
        "atomic_fetch_xor", "atomic_fetch_min", "atomic_fetch_max",
        
        // Synchronization
        "threadgroup_barrier", "simdgroup_barrier", "mem_flags",
        
        // Metal 4 - Raytracing functions (iOS 18+, macOS 15+)
        "trace_ray", "intersect_ray",
        "accept_hit_and_end_search", "ignore_intersection",
        "get_candidate_intersection_type",
        "get_world_ray_origin", "get_world_ray_direction",
        "get_ray_min_distance", "get_ray_max_distance",
        "get_triangle_barycentric_coord",
        "get_instance_id", "get_geometry_id", "get_primitive_id",
        "get_world_to_object_transform", "get_object_to_world_transform",
        
        // Metal 4 - Mesh shader functions
        "set_mesh_outputs", "set_primitive_count", "set_vertex_count",
        "set_index", "set_primitive_type",
        
        // Metal 4 - Advanced texture operations
        "sparse_sample", "sparse_read", "sparse_write",
        "get_sparse_texture_properties",
        
        // Metal 4 - Binary archive functions
        "create_binary_archive", "serialize_binary_archive",
        
        // Metal 4 - Mesh grid synchronization
        "mesh_grid_barrier"
    )
}
