package client.rendering;

import java.util.HashMap;

import static org.lwjgl.opengl.GL33.*;

public class Shader {
    private int handle;
    public Shader(String vertex, String fragment){
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertex);
        glCompileShader(vertexID);
        String log = glGetShaderInfoLog(vertexID);
        if(!log.isEmpty()){
            System.out.println("ERROR:VERTEX:COMPILATION:\n" + log);
        }

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragment);
        glCompileShader(fragmentID);
        log = glGetShaderInfoLog(fragmentID);
        if(!log.isEmpty()){
            System.out.println("ERROR:FRAGMENT:COMPILATION:\n" + log);
        }

        handle = glCreateProgram();
        glAttachShader(handle, vertexID);
        glAttachShader(handle, fragmentID);
        glLinkProgram(handle);

        log = glGetProgramInfoLog(fragmentID);
        if(!log.isEmpty()){
            System.out.println("ERROR:PROGRAM:LINKING:\n" + log);
        }

        glDetachShader(handle, vertexID);
        glDetachShader(handle, fragmentID);

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    public void bind() {
        glUseProgram(handle);
    }

    public void unbind() {
        glUseProgram(0);
    }
    private HashMap<String, Integer> uniforms = new HashMap<String, Integer>();
    public int getUniformLocation(String name){
        if(!uniforms.containsKey(name)){
            int loc = glGetUniformLocation(handle, name);
            uniforms.put(name, loc);
        }
        return uniforms.get(name);
    }
}
