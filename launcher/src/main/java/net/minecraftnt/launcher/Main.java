package net.minecraftnt.launcher;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.*;

public class Main {

    private static final String baseURL = "https://openability.tech/jimmster/minecraftnt/download/";

    public static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        System.out.println("Minecraftn't launcher");

        HashMap<String, String> flags = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("-")) {
                System.err.println("All flags have to start with \"-\"");
                return;
            }
            if (i + 1 == args.length) {
                flags.put(arg, "");
            } else {
                flags.put(arg.substring(1), args[i + 1]);
                i++;
            }
        }

        Object[] setflags = flags.keySet().toArray();
        ArrayList<String> flagsAllowed = new ArrayList<String>();
        flagsAllowed.add("version");
        flagsAllowed.add("username");
        //flagsAllowed.add("help");

        for (int i = 0; i < setflags.length; i++) {
            if (!flagsAllowed.contains(setflags[i])) {
                System.err.println("Invalid flags: " + setflags[i]);
                return;
            }
        }

        String version = flags.getOrDefault("version", VersionManager.getLatest());
        String username = flags.getOrDefault("username", "__unregistered__user__");

        System.exit(launchVersion(version, username));

    }

    private static int launchVersion(String version, String username) throws IOException, InterruptedException, ExecutionException, TimeoutException {

        if(version == null){
            System.err.println("Null version given, canceling launch!");
            return 1;
        }

        System.out.println("Launching version " + version);
        VersionManager.TryDownloadVersion(version);
        System.out.println("Launching!");
        int exitCode = LauncherWrapper.LaunchVersion(version, username);
        System.out.println("Exited with " + exitCode);
        return exitCode;
    }
}