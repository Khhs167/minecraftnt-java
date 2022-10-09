package net.minecraftnt.util.resources;

import java.io.InputStream;

public class Resources {
    public static byte[] loadResourceAsBytes(String name){

        byte[] folderBytes = FolderResources.loadResourceAsBytes(name);
        if(folderBytes != null)
            return folderBytes;

        byte[] classBytes = ClassResources.loadResourceAsBytes(name);
        if(classBytes != null)
            return classBytes;

        return null;
    }

    public static boolean fileExists(String name){

        if(FolderResources.fileExists(name))
            return true;

        if(ClassResources.fileExists(name))
            return true;

        return false;
    }

    public static String loadResourceAsString(String name){

        String folderString = FolderResources.loadResourceAsString(name);
        if(folderString != null)
            return folderString;

        String classString = ClassResources.loadResourceAsString(name);
        if(classString != null)
            return classString;

        return null;
    }

    public static InputStream loadResourceAsStream(String fileName) {

        InputStream folderStream= FolderResources.loadResourceAsStream(fileName);
        if(folderStream != null)
            return folderStream;

        InputStream classStream = ClassResources.loadResourceAsStream(fileName);
        if(classStream != null)
            return classStream;

        return null;
    }
}
