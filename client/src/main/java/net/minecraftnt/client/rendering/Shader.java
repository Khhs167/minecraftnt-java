package net.minecraftnt.client.rendering;

import net.minecraftnt.client.data.resources.Resources;
import net.minecraftnt.utility.Identifier;
import net.minecraftnt.utility.Registry;
import net.minecraftnt.utility.maths.Matrix4;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static org.lwjgl.opengl.GL33C.*;

public class Shader {

    public static final Registry<Shader> REGISTRY = new Registry<>();

    public static final Logger LOGGER = LogManager.getLogger(Shader.class);

    private static Shader current;

    private final int handle;
    private boolean exists = false;


    private static String getShader(Identifier identifier, String file){
        if(Resources.exists("assets/" + identifier.getNamespace() + "/shaders/" + identifier.getName() + file))
            return Resources.readString("assets/" + identifier.getNamespace() + "/shaders/" + identifier.getName() + file);
        return Resources.readString("assets/minecraftnt/shaders/default/" + file);
    }


    public static Shader load(Identifier identifier){
        Shader shader = new Shader(getShader(identifier, "vertex.vert"), getShader(identifier, "geometry.geom"), getShader(identifier, "fragment.frag"));

        REGISTRY.register(identifier, shader);

        return shader;
    }

    public static boolean bind(Identifier identifier) {

        Shader shader = REGISTRY.get(identifier);

        if(shader == null) {
            LOGGER.warn("Fetching shader {} returned null!", identifier.toString());
            return false;
        }

        shader.bind();
        current = shader;
        return true;
    }

    public static boolean setProjection(Matrix4 matrix) {
        if(current == null) {
            LOGGER.error("Cannot set projection of null shader");
            return false;
        }

        glUniformMatrix4fv(current.getUniformLocation("projection"), false, matrix.getData());

        return true;
    }

    public static boolean setView(Matrix4 matrix) {
        if(current == null) {
            LOGGER.error("Cannot set view of null shader");
            return false;
        }

        glUniformMatrix4fv(current.getUniformLocation("view"), false, matrix.getData());

        return true;
    }

    public static boolean setModel(Matrix4 matrix) {
        if(current == null) {
            LOGGER.error("Cannot set model of null shader");
            return false;
        }

        glUniformMatrix4fv(current.getUniformLocation("model"), false, matrix.getData());

        return true;
    }

    public Shader(String vertex, String geometry, String fragment){
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertex);
        glCompileShader(vertexID);
        String log = glGetShaderInfoLog(vertexID);
        if(!log.isEmpty()){
            LOGGER.error("Vertex shader compilation failed:\n" + log);
        }

        int geometryID = glCreateShader(GL_GEOMETRY_SHADER);
        glShaderSource(geometryID, geometry);
        glCompileShader(geometryID);
        log = glGetShaderInfoLog(geometryID);
        if(!log.isEmpty()){
            LOGGER.error("Geometry shader compilation failed:\n" + log);
        }

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragment);
        glCompileShader(fragmentID);
        log = glGetShaderInfoLog(fragmentID);
        if(!log.isEmpty()){
            LOGGER.error("Fragment shader compilation failed:\n" + log);
        }

        handle = glCreateProgram();
        glAttachShader(handle, vertexID);
        glAttachShader(handle, geometryID);
        glAttachShader(handle, fragmentID);
        glLinkProgram(handle);

        log = glGetProgramInfoLog(fragmentID);
        if(!log.isEmpty()){
            LOGGER.error("Shader linking failed:\n" + log);
        }

        glDetachShader(handle, vertexID);
        glDetachShader(handle, geometryID);
        glDetachShader(handle, fragmentID);

        glDeleteShader(vertexID);
        glDeleteShader(geometryID);
        glDeleteShader(fragmentID);

        exists = true;
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
        if(!exists) {
            LOGGER.fatal("Tried to dispose non-existent shader!");
            throw new IllegalStateException("Cannot dispose disposed shader");
        }

        glDeleteProgram(handle);
        exists = false;
    }
}
