package net.minecraftnt.client;

import net.minecraftnt.client.rendering.Window;
import net.minecraftnt.client.sound.SoundClip;
import net.minecraftnt.client.sound.SoundManager;
import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.performance.ThreadedMethodExecutor;
import net.minecraftnt.util.GameInfo;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.resources.ClassResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        Thread.currentThread().setName("Main");

        try {

            Files.writeString(Path.of(GameInfo.getGameDirectory(), "README.TXT"), Objects.requireNonNull(ClassResources.loadResourceAsString("assets/GAME_DIR_README.txt")));



            LOGGER.info("Starting Minecraftn't {}", GameInfo.version.toString());


            String user;
            String session = "-";

            user = "__unregistered__user__";

            // User setup
            if(args.length > 0)
                user = args[0];

            if(args.length > 1)
                session = args[1];

            LOGGER.info("Creating minecraftn't instance");

            SoundManager.initialize();



            ClientMainHandler.tryCreate();
            Minecraft.tryCreate();

            LOGGER.info("Running game");
            ClientMainHandler.run(new Session(user, session));

            SoundManager.getInstance().close();

            LOGGER.info("Halting ThreadedMethodExecutor!");
            ThreadedMethodExecutor.getExecutor().halt();
            LOGGER.info("Goodbye!");
        } catch (Exception e){

            if(ClientMainHandler.getInstance() != null)
                ClientMainHandler.getInstance().getWindow().close();

            StringWriter stringWriter = new StringWriter();
            PrintWriter stackTraceStream = new PrintWriter(stringWriter);

            e.printStackTrace(stackTraceStream);

            LOGGER.fatal("An error occurred: {}", stringWriter.toString());

            System.exit(1);
        }
        System.exit(0);
    }
}
