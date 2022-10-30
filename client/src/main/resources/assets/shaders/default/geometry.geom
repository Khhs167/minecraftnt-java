#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 5) out;
out vec3 color;

in QUAD_DATA {
    vec2 size;
    int orientation;
} gs_in[];

struct orientation_t {
    vec3 up;
    vec3 right;
};

const orientation_t orientations[1] = orientation_t[1]( orientation_t(vec3(0, 1, 0), vec3(1, 0, 0)) );


void main() {
    vec4 position = gl_in[0].gl_Position;
    vec2 size = gs_in[0].size;

    vec4 up = vec4(orientations[gs_in[0].orientation].up * size.y, 0);
    vec4 right = vec4(orientations[gs_in[0].orientation].right * size.x, 0);

    color = vec3(1); // gs_in[0] since there's only one input vertex
    gl_Position = position;    // 1:bottom-left
    EmitVertex();
    gl_Position = position + right;    // 2:bottom-right
    EmitVertex();
    gl_Position = position + up;    // 3:top-left
    EmitVertex();
    gl_Position = position + right + up;    // 4:top-right
    EmitVertex();
    EndPrimitive();
}