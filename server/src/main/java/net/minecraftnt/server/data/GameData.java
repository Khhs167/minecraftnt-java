package net.minecraftnt.server.data;

import java.io.File;
import java.nio.file.Path;

public class GameData {
    public static String getGameDirectory() {
        String home_dir = System.getProperty("user.home");

        Path path = Path.of(home_dir, ".khhs", "net/minecraftnt");

        File gameDir = path.toFile();

        if(!gameDir.exists())
            if(!gameDir.mkdirs())
                throw new RuntimeException("Could not create game directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getResourceDirectory() {
        Path path = Path.of(getGameDirectory(), "resources");

        File resourceDir = path.toFile();

        if(!resourceDir.exists())
            if(!resourceDir.mkdirs())
                throw new RuntimeException("Could not create resource directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getResourceLocation(String resource){
        return Path.of(getResourceDirectory(), resource).toAbsolutePath().toString();
    }

    public static String getModsDirectory() {
        Path path = Path.of(getGameDirectory(), "mods");

        File resourceDir = path.toFile();

        if(!resourceDir.exists())
            if(!resourceDir.mkdirs())
                throw new RuntimeException("Could not create mod directory!");

        return path.toAbsolutePath().toString();
    }
}
