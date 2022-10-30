package net.minecraftnt.client.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL33C.*;

public class Window {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private final long windowHandle;

    public Window(int width, int height, String title){
        this(width, height, title, null);
    }

    public Window(int width, int height, String title, Window parent){

        if(!glfwInit()){
            LOGGER.fatal("GLFW initialization failed!");
            throw new IllegalStateException("Could not initialize GLFW");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        windowHandle = glfwCreateWindow(width, height, title, NULL, (parent != null ? parent.windowHandle : NULL));

        if(windowHandle == NULL){
            LOGGER.fatal("Window creation failed!");
            throw new IllegalStateException("Could not initialize window!");
        }

        glfwMakeContextCurrent(windowHandle);

        glfwSwapInterval(1);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        //glEnable(GL_CULL_FACE);
        //glCullFace(GL_BACK);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0f, 0f, 0f, 1.0f);
        //glClearColor(0.5f, 0.8f, 1.0f, 1.0f);

    }

    public boolean stepFrame(){

        if(glfwWindowShouldClose(windowHandle))
            return false;

        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        return true;

    }

    public void dispose() {
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

}
