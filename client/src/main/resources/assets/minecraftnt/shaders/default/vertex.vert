#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in float lighting;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

uniform float time;
uniform float delta;

out struct VertexData {
    vec3 position;
    vec2 uv;
    float lighting;
} Vertex;

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0);

    Vertex.position = (model * vec4(position - vec3(0.5), 1.0)).rgb;
    Vertex.uv = uv;
    Vertex.lighting = lighting;
}