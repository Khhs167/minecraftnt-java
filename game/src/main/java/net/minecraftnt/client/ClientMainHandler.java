package net.minecraftnt.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import imgui.type.ImString;
import net.minecraftnt.client.rendering.*;
import net.minecraftnt.client.sound.SoundClip;
import net.minecraftnt.client.sound.SoundManager;
import net.minecraftnt.client.sound.SoundSource;
import net.minecraftnt.client.ui.fonts.Font;
import net.minecraftnt.client.voxels.VoxelInformation;
import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.entities.PlayerEntity;
import net.minecraftnt.server.entities.special.Pawn;
import net.minecraftnt.server.physics.PhysicsBody;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.util.*;
import net.minecraftnt.util.input.KeyboardInput;
import net.minecraftnt.util.input.MouseInput;
import net.minecraftnt.util.registries.Registry;
import net.minecraftnt.util.resources.CustomResources;
import net.minecraftnt.util.resources.PackInfo;
import net.minecraftnt.util.resources.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.util.ArrayList;
import java.util.Vector;

import static net.minecraftnt.client.rendering.DirectRenderer.*;
import static net.minecraftnt.client.rendering.DirectRenderer.DIRECTRENDERER_LINES;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class ClientMainHandler {
    private static ClientMainHandler theHandler = null;

    public static ClientMainHandler getInstance(){
        return theHandler;
    }

    public static Logger LOGGER = LogManager.getLogger(ClientMainHandler.class);

    public static void tryCreate(){
        if(theHandler == null)
            theHandler = new ClientMainHandler();
    }

    private ClientMainHandler() {
        camera = new Camera();
    }

    private static final KeyboardInput keyboard = new KeyboardInput();
    private static final MouseInput mouse = new MouseInput();

    public static final Identifier TERRAIN_ATLAS_IDENTIFIER = new Identifier("minecraft", "terrain");
    public static final Identifier TEST_SOUND = new Identifier("minecraft", "fuel");

    public static final int TERRAIN_ATLAS_TEXTURES = 16;
    public static final float TERRAIN_ATLAS_TEXTURE_SIZE = 1f / (float)TERRAIN_ATLAS_TEXTURES;

    public static final float BLOCK_HIGHLIGHT_OFFSET = 0.001f;


    public static KeyboardInput getKeyboardInput(){
        return keyboard;
    }
    public static MouseInput getMouseInput(){
        return mouse;
    }

    private static float GuiScale = 1f;

    public static float getGuiScale() {
        return GuiScale;
    }

    public static void setGuiScale(float guiScale) {
        GuiScale = guiScale;
    }

    private Pawn currentPawn;
    private Vector3 placementPosition;
    private static ThreadDownloadResources threadDownloadResources;
    private static Session session;
    private static PackInfo currentPackInfo;

    public static final Gson GSON = new GsonBuilder().create();

    public Vector3 getPlacementPosition() {
        return placementPosition;
    }

    public static void run(Session session) {
        LOGGER.info("Launching client with username '{}', session id '{}'", session.getUsername(), session.getId());
        ClientMainHandler.session = session;

        if(!session.validate())
            LOGGER.error("Session is not valid! Expect reduced functionality!");

        new Window().run();
    }

    public static Session getSession() {
        return session;
    }

    public static PackInfo getCurrentPackInfo() {
        return currentPackInfo;
    }

    public void setPlacementPosition(Vector3 pos){
        placementPosition = pos;
    }

    public void loadResources() {

        LOGGER.info("Generating shaders...");
        Registry.SHADERS.add(Shader.SHADER_BASE, Shader.LoadFromName("default"), true);

        Registry.SHADERS.add(Shader.SHADER_FONT, Shader.LoadFromName("font"), true);

        Registry.SHADERS.add(Shader.SHADER_PLACE, Shader.LoadFromName("placement"), true);

        Registry.SHADERS.add(Shader.SHADER_DIRECT, Shader.LoadFromName("direct"), true);

        Registry.TEXTURES.add(TERRAIN_ATLAS_IDENTIFIER, Texture.loadTexture("assets/terrain.png"), true);

        Registry.TEXTURES.add(Font.FONT_DEFAULT_TEXTURE, Texture.loadTexture("assets/font.png"), true);

        Registry.TEXTURES.add(PackInfo.PACK_IMAGE, Texture.loadTexture("pack.png"), true);

        Registry.FONTS.add(Font.FONT_DEFAULT, new Font(), true);

        currentPackInfo = GSON.fromJson(Resources.loadResourceAsString("pack.json"), PackInfo.class);


        SoundClip.loadSoundClip(TEST_SOUND, "assets/sound/mixer.ogg");

    }

    private SoundSource musicSource;

    public void loadClientSide(){

        LOGGER.info("Starting resource downloading...");

        threadDownloadResources = new ThreadDownloadResources();
        threadDownloadResources.start();


        Registry.TEXTURES.add(Texture.TEXTURE_NULL, Texture.loadTexture("assets/null.png"));

        loadResources();

        currentPawn = (PlayerEntity) Minecraft.getInstance().getWorld().createEntity(new Vector3(0, 80, 0), PlayerEntity.IDENTIFIER);

        setPlacementPosition(new Vector3(1, 50, 1));

        Minecraft.getInstance().getWorld().initGraphics();

        musicSource = SoundManager.getInstance().play(TEST_SOUND, true, Vector3.zero());


    }


    public void update(){

        threadDownloadResources.tryReload();

        musicSource.setPosition(Vector3.zero());
        musicSource.setSpeed(Vector3.zero());
        musicSource.setProperty(AL11.AL_REFERENCE_DISTANCE, 10f);
        musicSource.setProperty(AL11.AL_MAX_DISTANCE, 20f);
        SoundManager.getInstance().setListenerPosition(currentPawn.getTransform().location);
        SoundManager.getInstance().setListenerVelocity(currentPawn.getPhysicsBody().getVelocity());
        SoundManager.getInstance().setListenerOrientation(camera.getForward(), camera.getRight());

        if(currentPawn != null)
            currentPawn.translateCamera(camera);

        if(keyboard.isKeyDown(KeyboardInput.KEY_CLOSE))
            glfwSetWindowShouldClose(window.getHandle(), true);


        if(keyboard.isKeyDown(KeyboardInput.KEY_FREE_MOUSE)) {
            glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            isMouseFocused = false;
        }


        keyboard.update();
        mouse.update();
    }

    private boolean debugRender = true;
    private boolean msaa;

    public void render(){


        Texture.use(TERRAIN_ATLAS_IDENTIFIER);
        Minecraft.getInstance().getWorld().render();

        drBeginDraw();
        drLineWidth(5f);
        drVertexColour(Vector3.zero());

        drBeginVertex();
        drVertexPosition(placementPosition.sub(Vector3.one().multiply(-BLOCK_HIGHLIGHT_OFFSET)));
        drEndVertex();

        drBeginVertex();
        drVertexPosition(placementPosition.add(Vector3.up()).add(Vector3.one().multiply(BLOCK_HIGHLIGHT_OFFSET)));
        drEndVertex();

        drEndDraw(DIRECTRENDERER_LINES);



        Registry.FONTS.get(Font.FONT_DEFAULT).renderText("Hello World!!!\nThis is all done using bitmap fonts.", new Vector2(20, 100));

        if(debugRender) {
            // Draw chunk borders
            {
            drBeginDraw();
            drLineWidth(2f);

            Vector3 colorChunk = Vector3.forward();
            drVertexColour(colorChunk);
            for (int x = 0; x < Minecraft.getInstance().getWorld().chunks.length; x++) {
                for (int y = 0; y < Minecraft.getInstance().getWorld().chunks[x].length; y++) {
                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH, -Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH);
                    drEndVertex();
                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH);
                    drEndVertex();

                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH, -Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH);
                    drEndVertex();
                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH);
                    drEndVertex();

                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH, -Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH);
                    drEndVertex();
                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH);
                    drEndVertex();

                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH, -Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH);
                    drEndVertex();
                    drBeginVertex();
                    drVertexPosition(x * Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, y * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH);
                    drEndVertex();
                }
            }
            drEndDraw(DIRECTRENDERER_LINES);
        }

            // Draw player velocity
            if(currentPawn != null && currentPawn.getPhysicsBody() != null) {

                drBeginDraw();

                drLineWidth(5f);

                drBeginVertex();
                drVertexPosition(camera.getTransform().location.add(camera.getForward()));
                drVertexColour(currentPawn.getPhysicsBody().getVelocity().normalize().absolute());
                drEndVertex();
                drBeginVertex();
                drVertexPosition(camera.getTransform().location.add(currentPawn.getPhysicsBody().getVelocity().normalize().multiply(0.1f)).add(camera.getForward()));
                drVertexColour(currentPawn.getPhysicsBody().getVelocity().normalize().absolute());
                drEndVertex();
                drEndDraw(DIRECTRENDERER_LINES);
            }

        }

        // ImGUI debug info
        if(ImGui.begin("Debug")){
            ImGui.text("Entities: " + Minecraft.getInstance().getWorld().getEntityCount());


            if(ImGui.collapsingHeader("Resources")) {
                ImGui.indent();

                if(ImGui.button("Reload resources")){
                    loadResources();
                }

                boolean disabledDownload = threadDownloadResources.isAlive();
                if (disabledDownload)
                    ImGui.beginDisabled();

                if (ImGui.button("Download resources")) {
                    threadDownloadResources = new ThreadDownloadResources();
                    threadDownloadResources.start();
                }

                if (disabledDownload)
                    ImGui.endDisabled();

                ImString customPackName = new ImString();
                customPackName.set(CustomResources.getResourcePack());

                if(ImGui.inputText("Custom pack", customPackName)) {
                    CustomResources.setResourcePack(customPackName.get());
                }

                if(ImGui.collapsingHeader("Current Info")) {
                    ImGui.indent();

                    ImGui.image(Texture.get(PackInfo.PACK_IMAGE).getId(), 128, 128);

                    ImGui.text(currentPackInfo.name);
                    ImGui.newLine();
                    ImGui.text(currentPackInfo.description);
                    ImGui.newLine();
                    ImGui.text("Version: " + currentPackInfo.pack_version);

                    ImGui.unindent();
                }

                ImGui.unindent();

            }

            if(ImGui.collapsingHeader("Rendering")) {
                ImGui.indent();


                if (ImGui.button("MSAA: " + msaa)) {
                    msaa = !msaa;
                    if (msaa)
                        glEnable(GL_MULTISAMPLE);
                    else
                        glDisable(GL_MULTISAMPLE);
                }
                if (ImGui.button("Debug rendering: " + debugRender)) {
                    debugRender = !debugRender;
                }
                ImGui.text( 1 / Minecraft.getInstance().getDeltaTime() + " FPS");

                ImGui.unindent();
            }

            if(ImGui.collapsingHeader("Pawn")){
                ImGui.indent();
                if (ImGui.collapsingHeader("Position")) {
                    ImGui.indent();
                    ImGui.textDisabled(
                            "X: " + currentPawn.getTransform().location.getX() +
                                    "\nY: " + currentPawn.getTransform().location.getY() +
                                    "\nZ: " + currentPawn.getTransform().location.getZ()
                    );

                    ImGui.unindent();
                }

                if (ImGui.collapsingHeader("Rotation")) {
                    ImGui.indent();
                    ImGui.textDisabled(
                            "X: " + currentPawn.getTransform().rotation.getX() +
                                    "\nY: " + currentPawn.getTransform().rotation.getY() +
                                    "\nZ: " + currentPawn.getTransform().rotation.getZ()
                    );
                    ImGui.unindent();
                }
                if(currentPawn.getPhysicsBody() != null) {
                    PhysicsBody physicsBody = currentPawn.getPhysicsBody();
                    if (ImGui.collapsingHeader("Velocity")) {
                        ImGui.indent();
                        ImGui.textDisabled(
                                "X: " + physicsBody.getVelocity().getX() +
                                        "\nY: " + physicsBody.getVelocity().getY() +
                                        "\nZ: " + physicsBody.getVelocity().getZ()
                        );
                        ImGui.unindent();
                    }
                }

                if(ImGui.button("Reset position")){
                    currentPawn.setPosition(Vector3.up().multiply(80f));
                    if(currentPawn.getPhysicsBody() != null)
                        currentPawn.getPhysicsBody().setVelocity(Vector3.zero());
                }

                ImGui.unindent();
            }

            final boolean wasFocused = isMouseFocused;
            if(wasFocused)
                ImGui.beginDisabled();
            if(ImGui.button("Refocus mouse")){
                glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                isMouseFocused = true;
            }

            if(wasFocused)
                ImGui.endDisabled();
            var scale = new int[] { (int)getGuiScale() };
            if(ImGui.sliderInt("GUI scale", scale, 1, 32)){
                setGuiScale(scale[0]);
            }

        }
        ImGui.end();

    }

    private boolean isMouseFocused = true;

    public void setWindow(Window newWindow){
        window = newWindow;
    }

    private final Camera camera;

    public Camera getCamera() {
        return camera;
    }

    private Window window;
    public Window getWindow(){
        return window;
    }

}
