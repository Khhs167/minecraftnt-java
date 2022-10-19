package net.minecraftnt.util.registries;
import net.minecraftnt.client.rendering.Shader;
import net.minecraftnt.client.rendering.Texture;
import net.minecraftnt.client.sound.SoundClip;
import net.minecraftnt.client.ui.fonts.Font;
import net.minecraftnt.server.blocks.Block;
import net.minecraftnt.server.entities.Entity;
import net.minecraftnt.server.world.generators.IRWorldGenerator;
import net.minecraftnt.util.Identifier;

import java.util.Collection;
import java.util.HashMap;

public class Registry<T>{
    private final HashMap<Identifier, T> map;
    private Registry(){
        map = new HashMap<>();
    }

    public T add(Identifier identifier, T registrable, boolean override){
        if(map.containsKey(identifier) && !override)
            return registrable;
        map.put(identifier, registrable);
        return registrable;
    }

    public T add(Identifier identifier, T registrable){
        return add(identifier, registrable, true);
    }

    public Identifier addIdentifier(String namespace, String name, T registrable){
        return addIdentifier(namespace, name, registrable, false);
    }

    public Identifier addIdentifier(String namespace, String name, T registrable, boolean override){
        Identifier identifier = new Identifier(namespace, name);
        add(identifier, registrable, override);
        return identifier;
    }

    public T get(Identifier identifier){
        return map.get(identifier);
    }

    public Collection<T> getValues() {
        return map.values();
    }

    public static final Registry<IRWorldGenerator> WORLD_GENERATORS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();
    public static final Registry<Texture> TEXTURES = new Registry<>();
    public static final Registry<Class<? extends Entity>> ENTITIES = new Registry<>();
    public static final Registry<Font> FONTS = new Registry<>();
    public static final Registry<Shader> SHADERS = new Registry<>();
    public static final Registry<Integer> KEYBOARD_MAP = new Registry<>();
    public static final Registry<SoundClip> SOUNDS = new Registry<>();
}
