package server;

import client.Camera;
import client.rendering.Mesh;
import client.rendering.Shader;
import client.rendering.Texture;
import client.rendering.Window;
import client.ui.fonts.Font;
import client.voxels.VoxelInformation;
import imgui.ImGui;
import server.entities.PlayerEntity;
import server.entities.special.Pawn;
import server.world.World;
import server.world.generators.FlatWorldGen;
import server.world.generators.IRWorldGenerator;
import server.world.generators.Overworld;
import util.*;
import util.input.KeyboardInput;
import util.input.MouseInput;
import util.registries.Registry;
import util.resources.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Minecraft {
    private static Minecraft theMinecraft = null;
    private static final KeyboardInput keyboard = new KeyboardInput();
    private static final MouseInput mouse = new MouseInput();

    public static final Identifier TERRAIN_ATLAS_IDENTIFIER = new Identifier("minecraft", "terrain");

    public static final int TERRAIN_ATLAS_TEXTURES = 16;
    public static final float TERRAIN_ATLAS_TEXTURE_SIZE = 1f / (float)TERRAIN_ATLAS_TEXTURES;


    public static KeyboardInput getKeyboardInput(){
        return keyboard;
    }
    public static MouseInput getMouseInput(){
        return mouse;
    }

    public static Minecraft getInstance(){
        return theMinecraft;
    }

    private static float GuiScale = 1f;

    public static float getGuiScale() {
        return GuiScale;
    }

    public static void setGuiScale(float guiScale) {
        GuiScale = guiScale;
    }

    public static Version getVersion(){
        return new Version(0, 1, 0);
    }

    public static void tryCreate(){
        if(theMinecraft == null)
            theMinecraft = new Minecraft();
    }

    private Minecraft(){
        camera = new Camera();
    }

    private Map<Identifier, World> worlds = new HashMap<Identifier, World>();
    private Identifier currentWorld;

    private Pawn currentPawn;

    public World getWorld(){
        return worlds.get(currentWorld);
    }

    public void enterWorld(Identifier world){
        if(!worlds.containsKey(world)){
            World newWorld = new World();
            worlds.put(world, newWorld);
            currentWorld = world;
            newWorld.generate(Registry.WORLD_GENERATORS.get(world));

        }
        currentWorld = world;
    }

    private Mesh placementMesh;
    private Transform placementTransform;

    public void setPlacementPosition(Vector3 pos){
        placementTransform.location = pos.clone();
        placementTransform.location.setY(-placementTransform.location.getY());
    }

    public void loading(){
        System.out.println("Registering world generations... ");
        Registry.WORLD_GENERATORS.add(IRWorldGenerator.IDENTIFIER_FLAT, new FlatWorldGen());
        Registry.WORLD_GENERATORS.add(IRWorldGenerator.IDENTIFIER_OVERWORLD, new Overworld());

        Registry.ENTITIES.add(PlayerEntity.IDENTIFIER, PlayerEntity.class);
    }

    public void onLoaded() {

        System.out.println("Generating shaders...");
        Registry.SHADERS.add(Shader.SHADER_BASE, Shader.LoadFromName("default"));

        Registry.SHADERS.add(Shader.SHADER_FONT, Shader.LoadFromName("font"));

        Registry.SHADERS.add(Shader.SHADER_PLACE, Shader.LoadFromName("placement"));

        Registry.TEXTURE_ATLASES.add(TERRAIN_ATLAS_IDENTIFIER, new Texture("assets/terrain_mc.png"));
        Registry.TEXTURE_ATLASES.add(new Identifier("minecraft", "test"), new Texture("assets/test.png"));

        Registry.FONTS.add(Font.FONT_DEFAULT, new Font("assets/font.png"));

        System.out.println("Generating flat world");
        enterWorld(IRWorldGenerator.IDENTIFIER_OVERWORLD);
        System.out.println("Finished generation!");

        currentPawn = (PlayerEntity) getWorld().createEntity(new Vector3(0, 80, 0), PlayerEntity.IDENTIFIER);

        placementMesh = new Mesh();

        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<Vector2> uvs = new ArrayList<>();
        ArrayList<Integer> triangles = new ArrayList<Integer>();

        int vertexIndex = 0;
        Vector3 pos = new Vector3(0, 0, 0);
        for (int p = 0; p < 6; p++) {
            for (int i = 0; i < 6; i++) {

                int triangleIndex = VoxelInformation.voxelTris[p][i];
                vertices.add(VoxelInformation.voxelVerts[triangleIndex].add(pos));
                triangles.add(vertexIndex);

                uvs.add(VoxelInformation.voxelUvs[i]);

                vertexIndex++;

            }

        }
        placementMesh.vertices = vertices.toArray(Vector3[]::new);
        placementMesh.triangles = triangles.stream().mapToInt(i -> i).toArray();
        placementMesh.uv = uvs.toArray(Vector2[]::new);

        placementMesh.buildMesh();

        placementTransform = new Transform(Vector3.zero());


    }

    private boolean msaa = false;
    public void update(){
        deltaTime = (float) (glfwGetTime() - lastFrame);
        lastFrame = (float)glfwGetTime();

        getWorld().update();

        if(currentPawn != null)
            currentPawn.translateCamera(camera);

        if(keyboard.isKeyDown(GLFW_KEY_F4))
            glfwSetWindowShouldClose(window.getHandle(), true);


        if(keyboard.isKeyDown(GLFW_KEY_ESCAPE)) {
            glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            isMouseFocused = false;
        }


        keyboard.update();
        mouse.update();
    }

    private Mesh refMesh;
    private Transform refTrans;

    private float lastFrame = 0;
    private float deltaTime = 0;

    public float getDeltaTime() {
        return deltaTime;
    }

    public void render(){
        Registry.TEXTURE_ATLASES.get(TERRAIN_ATLAS_IDENTIFIER).use();
        getWorld().render();
        placementMesh.render(Registry.SHADERS.get(Shader.SHADER_PLACE), placementTransform);

        Registry.FONTS.get(Font.FONT_DEFAULT).renderText("HELLOWORLD", new Vector2(1, 0));

        // ImGUI debug info
        if(ImGui.begin("Debug")){
            ImGui.text("Entities: " + getWorld().getEntityCount());

            if(ImGui.collapsingHeader("Rendering")) {
                ImGui.indent();

                if (ImGui.button("MSAA: " + msaa)) {
                    msaa = !msaa;
                    if (msaa)
                        glEnable(GL_MULTISAMPLE);
                    else
                        glDisable(GL_MULTISAMPLE);
                }
                ImGui.text( 1 / getDeltaTime() + " FPS");

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

                if(currentPawn instanceof PlayerEntity){
                    PlayerEntity playerEntity = (PlayerEntity)currentPawn;
                    if (ImGui.collapsingHeader("Velocity")) {
                        ImGui.indent();
                        ImGui.textDisabled(
                                "X: " + playerEntity.getVelocity().getX() +
                                        "\nY: " + playerEntity.getVelocity().getY() +
                                        "\nZ: " + playerEntity.getVelocity().getZ()
                        );
                        ImGui.unindent();
                    }
                }

                if(ImGui.button("Reset position")){
                    currentPawn.getTransform().location = Vector3.up().multiply(80f);
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
            var scale = new float[] { getGuiScale() };
            if(ImGui.sliderFloat("GUI scale", scale, 1, 4)){
                setGuiScale(scale[0]);
            }

            ImGui.end();
        }

    }

    private boolean isMouseFocused = true;

    public void setWindow(Window newWindow){
        window = newWindow;
    }

    private Camera camera;

    public Camera getCamera() {
        return camera;
    }

    private Window window;
    public Window getWindow(){
        return window;
    }
}
