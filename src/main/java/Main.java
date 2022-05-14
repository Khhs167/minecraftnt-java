import client.audio.AudioManager;
import client.rendering.Window;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import server.Minecraft;
import server.performance.ThreadedMethodExecutor;

public class Main {
    private long window;

    public static void main(String[] args) {
        Minecraft.tryCreate();
        AudioManager.getInstance();
        new Window().run();
        ThreadedMethodExecutor.getExecutor().halt();
    }
}
