#version 330

uniform sampler2D texture;

out vec4 FragColor;

in vec2 UV;
in vec3 color;

void main() {
    FragColor = texture2D(texture, UV).rgba;
}