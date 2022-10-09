#version 330 core
out vec4 FragColor;

uniform sampler2D texture;

in vec2 vertexUV; // the input variable from the vertex shader (same name and same type)
in float depth; // the input variable from the vertex shader (same name and same type)
in float lightning;

void main()
{
    vec4 color = texture2D(texture, vertexUV);

    float fogDepth = 100; // Replace the 40 to anything... 40 is also ok.
    vec4 fogColour = vec4(0.5f, 0.8f, 1.0f, 1.0f); // Choose anything

    float originalZ = gl_FragCoord.z / gl_FragCoord.w;
    float fog = clamp(pow(originalZ / fogDepth, 2), 0, 1);


    FragColor = mix(vec4(color.rgb * lightning, color.a), fogColour , fog);
}