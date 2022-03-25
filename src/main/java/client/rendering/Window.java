package client.rendering;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import server.Minecraft;
import util.Vector2;
import util.Vector2I;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL33.GL_DEPTH_BUFFER_BIT;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;
    private Vector2I size;

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private String glslVersion = null;

    public void run()
    {
        Minecraft.getInstance().setWindow(this);
        System.out.println("Initializing LWJGL " + Version.getVersion());

        Minecraft.getInstance().loading();
        init();
        Minecraft.getInstance().onLoaded();
        loop();

        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();

        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public long getHandle() {
        return handle;
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glslVersion = "#version 330";

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_SAMPLES, 4);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        handle = glfwCreateWindow(1280, 720, "Minecraftn't " + Minecraft.getVersion().toString(), NULL, NULL);
        size = new Vector2I(1280, 720);
        if ( handle == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(handle, this::keyCallback);
        glfwSetCursorPosCallback(handle, this::mouseCallback);
        glfwSetWindowSizeCallback(handle, this::resizeCallback);
        glfwSetMouseButtonCallback(handle, this::mouseClickCallback);

        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);
        glfwShowWindow(handle);
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glDisable(GL_MULTISAMPLE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ImGui.createContext();

        imGuiGlfw.init(handle, true);
        imGuiGl3.init(glslVersion);


    }

    private void mouseClickCallback(long window, int button, int action, int mods) {
        if( action == GLFW_PRESS )
            Minecraft.getMouseInput().mouseClick(button);
        if( action == GLFW_RELEASE )
            Minecraft.getMouseInput().mouseRelease(button);
    }

    private void mouseCallback(long window, double x, double y) {
        Minecraft.getMouseInput().onInput(new Vector2((float)x, (float)y), this);
    }

    private void resizeCallback(long window, int width, int height) {
        glViewport(0, 0, width, height);
        this.size = new Vector2I(width, height);
    }

    public Vector2I getSize() {
        return size;
    }

    public float getRatio() {
        return (float)size.getX() / (float)size.getY();
    }

    private void keyCallback(long window, int key, int scancode, int action, int mods){
        if ( action == GLFW_PRESS )
            Minecraft.getKeyboardInput().keyPress(key);
        if ( action == GLFW_RELEASE )
            Minecraft.getKeyboardInput().keyRelease(key);
    }

    private void loop() {
        glClearColor(0.5f, 0.8f, 1.0f, 1.0f);

        while ( !glfwWindowShouldClose(handle) ) {
            Minecraft.getInstance().update();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            imGuiGlfw.newFrame();
            ImGui.newFrame();

            Minecraft.getInstance().render();
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }


            glfwSwapBuffers(handle);
            glfwPollEvents();
        }
    }


}
