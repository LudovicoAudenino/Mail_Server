package it.ludovico.server.service;

import javafx.application.Platform;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class LogService {
    
    private static Consumer<String> logHandler;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public static void setLogHandler(Consumer<String> handler) {
        logHandler = handler;
    }
    
    private static void addLog(String level, String message) {
        if (logHandler != null) {
            String timestamp = LocalDateTime.now().format(formatter);
            String logMessage = String.format("[%s] %s: %s", timestamp, level, message);
            Platform.runLater(() -> logHandler.accept(logMessage));
        }
    }
    
    public static void info(String message) {
        addLog("INFO", message);
    }
    
    public static void warn(String message) {
        addLog("WARN", message);
    }
    
    public static void error(String message) {
        addLog("ERROR", message);
    }
    
    public static void debug(String message) {
        addLog("DEBUG", message);
    }
    
    public static void log(String message) {
        info(message);
    }
}