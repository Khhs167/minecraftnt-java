package net.minecraftnt.client.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL33C.*;

public class Window {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private final long windowHandle;
    private int width, height;

    public Window(int width, int height, String title){
        this(width, height, title, null);
    }

    public Window(int width, int height, String title, Window parent){

        this.width = width;
        this.height = height;

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
        glClearColor(0.5f, 0.8f, 1.0f, 1.0f);

        glfwSetWindowSizeCallback(windowHandle, this::windowResizeCallback);

        previousTime = getTime();

    }

    private void windowResizeCallback(long window, int w, int h){
        this.width = w;
        this.height = h;
        glViewport(0, 0, w, h);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private float previousTime;
    private float frameTime;

    public float getFrameTime() {
        return frameTime;
    }

    public float getAspectRatio() {
        return (float)width / (float)height;
    }

    public boolean stepFrame(){

        frameTime = getTime() - previousTime;
        previousTime = getTime();

        if(glfwWindowShouldClose(windowHandle))
            return false;

        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        return true;

    }

    public void dispose() {
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    public float getTime() {
        return (float)glfwGetTime();
    }
}
