#version 330 core
/*layout (points) in;
layout (triangle_strip, max_vertices = 5) out;
out vec2 UV;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;


in QUAD_DATA {
    vec2 uv;
    vec2 uv_size;
    vec2 size;
    int orientation;
} gs_in[];

struct orientation_t {
    vec3 up;
    vec3 right;
};

const orientation_t orientations[6] = orientation_t[6](
    orientation_t(vec3(0, 1, 0), vec3(1, 0, 0)), // YX - front
    orientation_t(vec3(0, 1, 0), vec3(0, 0, 1)), // YZ - right
    orientation_t(vec3(0, 1, 0), vec3(1, 0, 0)), // YX - back
    orientation_t(vec3(0, 1, 0), vec3(0, 0, 1)), // YZ - left
    orientation_t(vec3(1, 0, 0), vec3(0, 0, 1)), // XZ - top
    orientation_t(vec3(1, 0, 0), vec3(0, 0, 1))  // XZ - bottom
);


void main() {
    vec4 position = gl_in[0].gl_Position;
    vec2 size = gs_in[0].size;

    vec2 uv = gs_in[0].uv;
    vec2 uv_size = gs_in[0].uv_size;

    vec4 up = vec4(orientations[gs_in[0].orientation].up * size.y, 0);
    vec4 right = vec4(orientations[gs_in[0].orientation].right * size.x, 0);

    gl_Position = projection * view * model * (position);    // 1:bottom-left
    UV = uv;
    EmitVertex();
    gl_Position = projection * view * model * (position + right);    // 2:bottom-right
    UV = uv + vec2(uv_size.x, 0);
    EmitVertex();
    gl_Position = projection * view * model * (position + up);    // 3:top-left
    UV = uv + vec2(0, uv_size.y);
    EmitVertex();
    gl_Position = projection * view * model * (position + right + up);    // 4:top-right
    UV = uv + uv_size;
    EmitVertex();
    EndPrimitive();
}*/