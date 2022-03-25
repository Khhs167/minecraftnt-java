import client.rendering.Window;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import server.Minecraft;

public class Main {
    private long window;

    public static void main(String[] args) {
        Minecraft.tryCreate();
        new Window().run();
    }
}
