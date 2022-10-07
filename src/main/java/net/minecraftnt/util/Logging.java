package net.minecraftnt.util;

import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class Logging {
    private static final String MESSAGE_PATTERN = "%-7p %d [%t] %c %x - %m%n";
    private static final PatternLayout LAYOUT = PatternLayout.newBuilder().withPattern(MESSAGE_PATTERN).build();
    private static final ConsoleAppender CONSOLE_APPENDER = ConsoleAppender.newBuilder().setLayout(LAYOUT).build();

    private static final Logger ROOT_LOGGER = LogManager.getRootLogger();
    public static void InitLogging(){


    }
}
