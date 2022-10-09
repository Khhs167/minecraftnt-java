#version 330 core

layout (location = 0) in vec3 pos; // the position variable has attribute position 0
layout (location = 1) in vec2 uv; // the position variable has attribute position 0
layout (location = 2) in float color; // the position variable has attribute position 0

out vec2 vertexUV; // specify a color output to the fragment shader
out float depth; // specify a color output to the fragment shader
out float lightning; // specify a color output to the fragment shader

uniform mat4 mat_projection;
uniform mat4 mat_view;
uniform mat4 mat_world;

void main()
{
    gl_Position = mat_projection * mat_view * mat_world * vec4(pos, 1.0); // see how we directly give a vec3 to vec4's constructor
    vec3 cam_world = vec3(inverse(mat_view) * vec4(0,0,0,1));
    depth = distance(cam_world, pos);
    vertexUV = uv; // set the output variable to a dark-red color
    lightning = color;
}