package net.minecraftnt.server.data.resources;

import net.minecraftnt.server.data.GameData;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FolderResources implements ResourceProvider {
    public boolean fileExists(String name){
        return new File(GameData.getResourceLocation(name)).exists();
    }
    public InputStream loadResourceAsStream(String fileName) {
        if(!fileExists(fileName))
            return null;

        try {
            return new FileInputStream(GameData.getResourceLocation(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getDirectoryContents(String path) {
        File[] files = new File(GameData.getResourceLocation(path)).listFiles();

        ArrayList<String> content = new ArrayList<>();

        if(files == null)
            return new String[0];

        for(File file : files) {
            if(file.isFile() && !file.getName().startsWith(".")) {
                content.add(file.getName());
            }
        }

        return content.toArray(new String[0]);
    }
}
