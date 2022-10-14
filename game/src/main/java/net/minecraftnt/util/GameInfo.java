package net.minecraftnt.util;

import java.io.File;
import java.nio.file.Path;

public class GameInfo {
    public static final Version version = new Version(1, 3, 0);

    public static String getGameDirectory() {
        String home_dir = System.getProperty("user.home");

        Path path = Path.of(home_dir, ".khhs", "minecraft");

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

    public static String getCustomDirectory() {
        Path path = Path.of(getGameDirectory(), "custom");

        File resourceDir = path.toFile();

        if(!resourceDir.exists())
            if(!resourceDir.mkdirs())
                throw new RuntimeException("Could not create custom directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getCustomDirectoryForPack(String pack) {
        Path path = Path.of(getCustomDirectory(), pack);

        File resourceDir = path.toFile();

        if(!resourceDir.exists())
            if(!resourceDir.mkdirs())
                throw new RuntimeException("Could not create custom pack directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getResourceLocation(String resource){
        return Path.of(getResourceDirectory(), resource).toAbsolutePath().toString();
    }

    public static String getResourceLocationForPack(String resource, String pack){
        return Path.of(getCustomDirectoryForPack(pack), resource).toAbsolutePath().toString();
    }
}
