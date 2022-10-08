package net.minecraftnt.launcher;

import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class LauncherWrapper {
    public static int LaunchVersion(String version, String username) throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder();

        System.out.println("Launching " + version + " with username " + username);


        String location = GameInfo.getVersionLocation(version);

        builder.command("java", "-jar", location + "/client.jar", username);

        System.out.println("Launching Minecraftn't with command " + String.join(" ", builder.command()));
        Process process = builder.start();
        StreamGobbler outGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        StreamGobbler errGobbler =
                new StreamGobbler(process.getErrorStream(), System.out::println);
        Future<?> future = Executors.newSingleThreadExecutor().submit(outGobbler);
        Future<?> future2 = Executors.newSingleThreadExecutor().submit(errGobbler);
        return process.waitFor();
    }
}
