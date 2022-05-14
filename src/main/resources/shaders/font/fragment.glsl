#version 330 core
out vec4 FragColor;

uniform sampler2D texture;

in vec2 vertexUV; // the input variable from the vertex shader (same name and same type)

void main()
{
    vec4 color = texture2D(texture, vertexUV);
    FragColor = color;
}