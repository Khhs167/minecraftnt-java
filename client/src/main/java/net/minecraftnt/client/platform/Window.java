package net.minecraftnt.client.platform;

import net.minecraftnt.util.InputDevice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBImaging.GL_TABLE_TOO_LARGE;
import static org.lwjgl.system.APIUtil.apiUnknownToken;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import static org.lwjgl.opengl.GL33C.*;

public class Window {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private final long windowHandle;
    private int width, height;
    private InputDevice inputDevice;

    public Window(int width, int height, String title){
        this(width, height, title, null);
    }

    public Window(int width, int height, String title, Window parent){
        LOGGER.info("Initializing GLFW window: ({}, {}, {}, {})", width, height, title, parent);
        LOGGER.info("Using LWJGL {}", org.lwjgl.Version.getVersion());
        this.width = width;
        this.height = height;

        if(!glfwInit()){
            LOGGER.fatal("GLFW initialization failed!");
            throw new IllegalStateException("Could not initialize GLFW");
        }

        LOGGER.info("Setting window OpenGL version to 3.3");
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        LOGGER.info("Creating window");
        windowHandle = glfwCreateWindow(width, height, title, NULL, (parent != null ? parent.windowHandle : NULL));

        if(windowHandle == NULL){
            LOGGER.fatal("Window creation failed!");
            throw new IllegalStateException("Could not initialize window!");
        }

        LOGGER.info("Setting up OpenGL and GLFW callbacks and information");
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                LOGGER.error("GLFW_{}: {}", error, getDescription(description));
            }
        });

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.5f, 0.8f, 1.0f, 1.0f);

        glfwSetWindowSizeCallback(windowHandle, this::windowResizeCallback);

        previousTime = getTime();

        LOGGER.info("Window initialized!");

        LOGGER.info("Creating input device");
        inputDevice = new InputDevice(GLFW_KEY_LAST);
        glfwSetKeyCallback(windowHandle, new GLFWKeyCallbackI() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                //LOGGER.info("Key press({}, {}, {}, {}, {})", window, key, scancode, action, mods);
                if(action == GLFW_PRESS) {
                    inputDevice.pressKey(scancode);
                }
                if(action == GLFW_RELEASE) {
                    inputDevice.releaseKey(scancode);
                }
            }
        });

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

    private float glErrorPollTime = 0;

    public boolean stepFrame(){
        if(glErrorPollTime >= 0.1f) {
            int error;
            while ((error = glGetError()) != GL_NO_ERROR) {
                LOGGER.error("GL_{}: {}", error, getErrorString(error));
            }

            glErrorPollTime = 0;
        }



        frameTime = getTime() - previousTime;
        previousTime = getTime();

        if(glfwWindowShouldClose(windowHandle))
            return false;

        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glErrorPollTime += getFrameTime();

        inputDevice.step();

        return true;

    }

    public void dispose() {
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    public float getTime() {
        return (float)glfwGetTime();
    }

    private String getErrorString(int errorCode) {
        return switch (errorCode) {
            case GL_NO_ERROR -> "No error";
            case GL_INVALID_ENUM -> "Enum argument out of range";
            case GL_INVALID_VALUE -> "Numeric argument out of range";
            case GL_INVALID_OPERATION -> "Operation illegal in current state";
            case GL_STACK_OVERFLOW -> "Command would cause a stack overflow";
            case GL_STACK_UNDERFLOW -> "Command would cause a stack underflow";
            case GL_OUT_OF_MEMORY -> "Not enough memory left to execute command";
            case GL_INVALID_FRAMEBUFFER_OPERATION -> "Framebuffer object is not complete";
            case GL_TABLE_TOO_LARGE -> "The specified table is too large";
            default -> apiUnknownToken(errorCode);
        };
    }

    public InputDevice getInputDevice() {
        return inputDevice;
    }
}
