package net.minecraftnt.server;
import net.minecraftnt.server.entities.PlayerEntity;
import net.minecraftnt.server.world.World;
import net.minecraftnt.server.world.generators.FlatWorldGen;
import net.minecraftnt.server.world.generators.IRWorldGenerator;
import net.minecraftnt.server.world.generators.OverWorldGenerator;
import net.minecraftnt.util.*;
import net.minecraftnt.util.registries.Registry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;


public class Minecraft {
    private static Minecraft theMinecraft = null;

    public static Minecraft getInstance(){
        return theMinecraft;
    }

    public static Logger LOGGER = LogManager.getLogger(Minecraft.class);


    public static void tryCreate(){
        if(theMinecraft == null)
            theMinecraft = new Minecraft();
    }

    private Map<Identifier, World> worlds = new HashMap<Identifier, World>();
    private Identifier currentWorld;

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

    public void loading(){
        LOGGER.info("Registering world generations... ");
        Registry.WORLD_GENERATORS.add(IRWorldGenerator.IDENTIFIER_FLAT, new FlatWorldGen());
        Registry.WORLD_GENERATORS.add(IRWorldGenerator.IDENTIFIER_OVERWORLD, new OverWorldGenerator());

        Registry.ENTITIES.add(PlayerEntity.IDENTIFIER, PlayerEntity.class);

        LOGGER.info("Generating flat world");
        enterWorld(IRWorldGenerator.IDENTIFIER_OVERWORLD);
        LOGGER.info("Finished generation!");
    }

    public void update(){
        long currentTime = getTime();
        delta = currentTime - lastTime;
        lastTime = getTime();

        getWorld().update();

    }


    private long lastTime;
    private long delta;
    private long getTime() {
        return System.currentTimeMillis();
    }
    public float getDeltaTime() {
        return delta / 1000f;
    }
}
