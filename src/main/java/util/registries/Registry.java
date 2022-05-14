package util.registries;
import client.rendering.Shader;
import client.rendering.Texture;
import client.ui.fonts.Font;
import server.blocks.Block;
import server.entities.Entity;
import server.world.generators.IRWorldGenerator;
import util.Identifier;

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

    public T get(Identifier identifier){
        return map.get(identifier);
    }

    public static final Registry<IRWorldGenerator> WORLD_GENERATORS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();
    public static final Registry<Texture> TEXTURE_ATLASES = new Registry<>();
    public static final Registry<Class<? extends Entity>> ENTITIES = new Registry<>();
    public static final Registry<Font> FONTS = new Registry<>();
    public static final Registry<Shader> SHADERS = new Registry<>();
}
