package net.minecraftnt.launcher;

import java.io.File;
import java.nio.file.Path;

public class GameInfo {
    public static String getGameDirectory() {
        String home_dir = System.getProperty("user.home");

        Path path = Path.of(home_dir, ".khhs", "minecraft");

        File gameDir = path.toFile();

        if(!gameDir.exists())
            if(!gameDir.mkdirs())
                throw new RuntimeException("Could not create game directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getVersionDirectory() {
        Path path = Path.of(getGameDirectory(), "versions");

        File resourceDir = path.toFile();

        if(!resourceDir.exists())
            if(!resourceDir.mkdirs())
                throw new RuntimeException("Could not create version directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getVersionLocation(String version){
        return Path.of(getVersionDirectory(), version).toAbsolutePath().toString();
    }
}
