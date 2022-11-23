package net.minecraftnt.client;

import net.minecraftnt.Registries;
import net.minecraftnt.api.ModLoadingImplementation;
import net.minecraftnt.client.rendering.Renderer;
import net.minecraftnt.client.builtin.VoxelGenerator;
import net.minecraftnt.rendering.Shader;
import net.minecraftnt.rendering.ShapeGenerator;
import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.util.Identifier;

public class GameLoader extends ModLoadingImplementation {
    @Override
    public void loadClient() {
        Renderer.shaderProviderC().load(Shader.DEFAULT);
        Renderer.textureProviderC().load(new Identifier("minecraftnt", "vroom"));
        Renderer.textureProviderC().load(new Identifier("minecraftnt", "4x4"));
        Renderer.textureProviderC().load(new Identifier("minecraftnt", "terrain"));

        Registries.SHAPE_GENERATOR.register(ShapeGenerator.BLOCK, new VoxelGenerator());

        TextureAtlasLocation.loadLocations("atlasmap");
    }
}
