package net.minecraftnt.client;

import net.minecraftnt.Registries;
import net.minecraftnt.api.ClientLoader;
import net.minecraftnt.client.rendering.gl.GLRenderer;
import net.minecraftnt.rendering.*;
import net.minecraftnt.client.builtin.VoxelGenerator;
import net.minecraftnt.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GameLoader implements ClientLoader {
    public static final Logger LOGGER = LogManager.getLogger(GameLoader.class);
    @Override
    public void loadClient() {
        LOGGER.info("Loading OpenGL API");
        Registries.RENDER_API.register(RenderAPI.OPENGL, new RenderAPI(0, new GLRenderer()));

        LOGGER.info("Loading shaders");
        Renderer.shaderProviderC().load(Shader.DEFAULT);

        LOGGER.info("Loading ShapeGenerators");
        Registries.SHAPE_GENERATOR.register(ShapeGenerator.BLOCK, new VoxelGenerator());

    }

    @Override
    public void loadResources() {
        LOGGER.info("Loading textures");
        Renderer.textureProviderC().load(new Identifier("minecraftnt", "vroom"));
        Renderer.textureProviderC().load(new Identifier("minecraftnt", "terrain"));
        TextureAtlasLocation.loadLocations("atlasmap");
    }
}
