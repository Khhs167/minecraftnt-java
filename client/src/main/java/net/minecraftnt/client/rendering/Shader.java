package net.minecraftnt.client.rendering;

import net.minecraftnt.client.data.resources.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static org.lwjgl.opengl.GL33C.*;

public class Shader {
    public static final Logger LOGGER = LogManager.getLogger(Shader.class);

    private int handle;

    public static Shader loadFromName(String name){
        String fShader = "";
        if(Resources.exists("assets/shaders/" + name + "/fragment.frag"))
            fShader =  Resources.readString("assets/shaders/" + name + "/fragment.frag");
        else
            fShader = Resources.readString("assets/shaders/default/fragment.frag");

        String gShader = "";
        if(Resources.exists("assets/shaders/" + name + "/geometry.geom"))
            gShader =  Resources.readString("assets/shaders/" + name + "/geometry.geom");
        else
            gShader = Resources.readString("assets/shaders/default/geometry.geom");

        String vShader = "";
        if(Resources.exists("assets/shaders/" + name + "/vertex.vert"))
            vShader = Resources.readString("assets/shaders/" + name + "/vertex.vert");
        else
            vShader = Resources.readString("assets/shaders/default/vertex.vert");



        return new Shader(vShader, gShader, fShader);
    }

    public Shader(String vertex, String geometry, String fragment){
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertex);
        glCompileShader(vertexID);
        String log = glGetShaderInfoLog(vertexID);
        if(!log.isEmpty()){
            LOGGER.error("ERROR:VERTEX:COMPILATION:\n" + log);
        }

        int geometryID = glCreateShader(GL_GEOMETRY_SHADER);
        glShaderSource(geometryID, geometry);
        glCompileShader(geometryID);
        log = glGetShaderInfoLog(geometryID);
        if(!log.isEmpty()){
            LOGGER.error("ERROR:GEOMETRY:COMPILATION:\n" + log);
        }

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragment);
        glCompileShader(fragmentID);
        log = glGetShaderInfoLog(fragmentID);
        if(!log.isEmpty()){
            LOGGER.error("ERROR:FRAGMENT:COMPILATION:\n" + log);
        }

        handle = glCreateProgram();
        glAttachShader(handle, vertexID);
        glAttachShader(handle, geometryID);
        glAttachShader(handle, fragmentID);
        glLinkProgram(handle);

        log = glGetProgramInfoLog(fragmentID);
        if(!log.isEmpty()){
            LOGGER.error("ERROR:PROGRAM:LINKING:\n" + log);
        }

        glDetachShader(handle, vertexID);
        glDetachShader(handle, geometryID);
        glDetachShader(handle, fragmentID);

        glDeleteShader(vertexID);
        glDeleteShader(geometryID);
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


    public void dispose()  {
        glDeleteProgram(handle);
    }
}
