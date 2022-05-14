package client.rendering;

import util.Identifier;
import util.resources.Resources;

import java.util.HashMap;

import static org.lwjgl.opengl.GL33.*;

public class Shader {

    public static final Identifier SHADER_BASE = new Identifier("minecraft", "shader_base");
    public static final Identifier SHADER_PLACE = new Identifier("minecraft", "shader_place");
    public static final Identifier SHADER_FONT = new Identifier("minecraft", "shader_font");

    private int handle;

    public static Shader LoadFromName(String name){
        String fShader = "";
        if(Resources.fileExists("shaders/" + name + "/fragment.glsl"))
            fShader = Resources.loadResourceAsString("shaders/" + name + "/fragment.glsl");
        else
            fShader = Resources.loadResourceAsString("shaders/default/fragment.glsl");

        String vShader = "";
        if(Resources.fileExists("shaders/" + name + "/vertex.glsl"))
            vShader = Resources.loadResourceAsString("shaders/" + name + "/vertex.glsl");
        else
            vShader = Resources.loadResourceAsString("shaders/default/vertex.glsl");

        return new Shader(vShader, fShader);
    }

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
