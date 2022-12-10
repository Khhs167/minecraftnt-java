package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.Registries;
import net.minecraftnt.rendering.ShaderProvider;
import net.minecraftnt.rendering.Shader;
import net.minecraftnt.server.data.resources.Resources;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Matrix4;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL33C.*;

public class GLShaderProvider extends ShaderProvider {

    public static final Logger LOGGER = LogManager.getLogger(GLShaderProvider.class);
    private static final LinkedList<Shader> SHADERS = new LinkedList<>();

    private static GLShader current;


    private static String getShader(Identifier identifier, String file){
        if(Resources.exists("assets/" + identifier.getNamespace() + "/shaders/" + identifier.getName() + "/" + file))
            return Resources.readString("assets/" + identifier.getNamespace() + "/shaders/" + identifier.getName() + "/" + file);
        return Resources.readString("assets/minecraftnt/shaders/default/" + file);
    }


    public Shader load(Identifier identifier){
        LOGGER.info("Creating shader {}", identifier);

        String vertex = getShader(identifier, "vertex.vert");
        String fragment = getShader(identifier, "fragment.frag");

        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        LOGGER.debug("Compiling vertex shader {}", vertexID);
        glShaderSource(vertexID, vertex);
        glCompileShader(vertexID);
        String log = glGetShaderInfoLog(vertexID);
        if(!log.isEmpty()){
            LOGGER.error("Vertex shader compilation failed:\n" + log);
        }

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        LOGGER.debug("Compiling fragment shader {}", fragmentID);
        glShaderSource(fragmentID, fragment);
        glCompileShader(fragmentID);
        log = glGetShaderInfoLog(fragmentID);
        if(!log.isEmpty()){
            LOGGER.error("Fragment shader compilation failed:\n" + log);
        }

        int handle = glCreateProgram();
        glAttachShader(handle, vertexID);
        glAttachShader(handle, fragmentID);
        LOGGER.debug("Linking shader {}", handle);
        glLinkProgram(handle);

        log = glGetProgramInfoLog(handle);
        if(!log.isBlank() || !log.isEmpty()){
            LOGGER.error("GLShaderProvider linking failed:\n" + log);
        }

        glDetachShader(handle, vertexID);
        glDetachShader(handle, fragmentID);

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);

        Shader shader = new GLShader(handle);

        Registries.SHADER.register(identifier, shader);
        SHADERS.add(shader);

        return shader;
    }

    public boolean bind(Identifier identifier) {

        GLShader shader = (GLShader)Registries.SHADER.get(identifier);

        if(shader == null) {
            LOGGER.warn("Fetching shader {} returned null!", identifier.toString());
            return false;
        }

        glUseProgram(shader.getId());
        current = shader;
        return true;
    }

    public boolean setFloat(String name, float value) {
        if(current == null) {
            LOGGER.error("Cannot set float uniform of null shader");
            return false;
        }

        glUniform1f(glGetUniformLocation(current.getId(), name), value);

        return true;
    }

    public boolean setProjection(Matrix4 matrix) {
        if(current == null) {
            LOGGER.error("Cannot set projection of null shader");
            return false;
        }

        glUniformMatrix4fv(glGetUniformLocation(current.getId(), "projection"), false, matrix.getData());

        return true;
    }

    public boolean setView(Matrix4 matrix) {
        if(current == null) {
            LOGGER.error("Cannot set view of null shader");
            return false;
        }

        glUniformMatrix4fv(glGetUniformLocation(current.getId(), "view"), false, matrix.getData());

        return true;
    }

    public boolean setModel(Matrix4 matrix) {
        if(current == null) {
            LOGGER.error("Cannot set model of null shader");
            return false;
        }

        glUniformMatrix4fv(glGetUniformLocation(current.getId(), "model"), false, matrix.getData());

        return true;
    }


    public void dispose()  {
        for(Shader _shader : SHADERS) {
            GLShader shader = (GLShader)_shader;
            LOGGER.info("Disposing shader " + shader.getId());
            glDeleteProgram(shader.getId());
        }


    }
}
