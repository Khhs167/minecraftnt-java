package net.minecraftnt.util.registries;
import net.minecraftnt.client.rendering.Shader;
import net.minecraftnt.client.rendering.Texture;
import net.minecraftnt.client.ui.fonts.Font;
import net.minecraftnt.server.blocks.Block;
import net.minecraftnt.server.entities.Entity;
import net.minecraftnt.server.world.generators.IRWorldGenerator;
import net.minecraftnt.util.Identifier;

import java.util.HashMap;

public class Registry<T>{
    private HashMap<Identifier, T> map;
    private Registry(){
        map = new HashMap<>();
    }

    public T add(Identifier identifier, T registrable){
        if(map.containsKey(identifier))
            return registrable;
        map.put(identifier, registrable);
        return registrable;
    }

    public Identifier addIdentifier(String namespace, String name, T registrable){
        Identifier identifier = new Identifier(namespace, name);
        add(identifier, registrable);
        return identifier;
    }

    public T get(Identifier identifier){
        return map.get(identifier);
    }

    public static final Registry<IRWorldGenerator> WORLD_GENERATORS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();
    public static final Registry<Texture> TEXTURE_ATLASES = new Registry<>();
    public static final Registry<Class<? extends Entity>> ENTITIES = new Registry<>();
    public static final Registry<Font> FONTS = new Registry<>();
    public static final Registry<Shader> SHADERS = new Registry<>();
    public static final Registry<Integer> KEYBOARD_MAP = new Registry<>();
}
