package com.kwizera.notificationservice.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLogger {
    public enum LogLevel {
        INFO, DEBUG, ERROR, WARN
    }

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String color = switch (level) {
            case INFO -> GREEN;
            case DEBUG -> BLUE;
            case ERROR -> RED;
            case WARN -> YELLOW;
            default -> RESET;
        };

        System.out.println(color + "LOG > [" + timestamp + "] [" + level + "] " + message);
    }
}
