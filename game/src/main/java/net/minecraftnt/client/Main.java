package net.minecraftnt.client;

import net.minecraftnt.client.rendering.Window;
import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.performance.ThreadedMethodExecutor;
import net.minecraftnt.util.GameInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        Window window = null;
        try {

            LOGGER.info("Starting Minecraftn't {}", GameInfo.version.toString());


            String user = "";
            String session = "-";

            user = "__unregistered__user__";

            // User setup
            if(args.length > 0)
                user = args[0];

            if(args.length > 1)
                session = args[1];

            LOGGER.info("Creating minecraftn't instance");

            ClientMainHandler.tryCreate();
            Minecraft.tryCreate();

            LOGGER.info("Running game");
            ClientMainHandler.run(new Session(user, session));

            LOGGER.info("Halting ThreadedMethodExecutor!");
            ThreadedMethodExecutor.getExecutor().halt();
            LOGGER.info("Goodbye!");
        } catch (Exception e){
            if(window != null)
                window.close();

            StringWriter stringWriter = new StringWriter();
            PrintWriter stackTraceStream = new PrintWriter(stringWriter);

            e.printStackTrace(stackTraceStream);

            LOGGER.fatal("An error occurred: {}", stringWriter.toString());

            System.exit(1);
        }
        System.exit(0);
    }
}
