package it.ludovico.server.service;

import javafx.application.Platform;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Centralized logging service for the JavaFX Mail System server.
 * 
 * <p>This service provides a unified logging interface for the entire server application,
 * with support for different log levels and automatic timestamp formatting. It uses a
 * configurable handler pattern to allow different log destinations (UI, console, file, etc.).</p>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Multiple Log Levels:</strong> INFO, WARN, ERROR, and DEBUG levels</li>
 *   <li><strong>Automatic Timestamps:</strong> All log messages include formatted timestamps</li>
 *   <li><strong>JavaFX Integration:</strong> Thread-safe UI updates via Platform.runLater()</li>
 *   <li><strong>Configurable Handler:</strong> Pluggable log destination via Consumer interface</li>
 *   <li><strong>Static Interface:</strong> Convenient static methods for easy usage throughout the application</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Configure log handler (typically done once at startup)
 * LogService.setLogHandler(message -> System.out.println(message));
 * 
 * // Use throughout the application
 * LogService.info("Server started successfully");
 * LogService.warn("Connection timeout detected");
 * LogService.error("Failed to save mailbox: " + e.getMessage());
 * }</pre>
 * 
 * <h2>Thread Safety</h2>
 * <p>This service is thread-safe and can be used from any thread. Log messages are
 * automatically scheduled on the JavaFX Application Thread when the handler is invoked,
 * making it safe for UI updates.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
public class LogService {
    
    /**
     * The configurable log message handler.
     * Typically set to update the server UI's log display.
     */
    private static Consumer<String> logHandler;
    
    /**
     * Date/time formatter for log message timestamps.
     * Uses HH:mm:ss format for compact time display.
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Sets the log message handler for processing log output.
     * 
     * <p>The handler is typically configured once during application startup
     * to specify where log messages should be sent (UI display, console, file, etc.).
     * The handler receives formatted log messages including timestamps and log levels.</p>
     * 
     * @param handler the consumer that will receive formatted log messages; may be null to disable logging
     */
    public static void setLogHandler(Consumer<String> handler) {
        logHandler = handler;
    }
    
    /**
     * Internal method for formatting and dispatching log messages.
     * 
     * <p>This method creates a formatted log message with timestamp and log level,
     * then schedules the handler execution on the JavaFX Application Thread to
     * ensure thread-safe UI updates.</p>
     * 
     * <p>Message format: {@code [HH:mm:ss] LEVEL: message}</p>
     * 
     * @param level the log level (INFO, WARN, ERROR, DEBUG)
     * @param message the log message content
     */
    private static void addLog(String level, String message) {
        if (logHandler != null) {
            String timestamp = LocalDateTime.now().format(formatter);
            String logMessage = String.format("[%s] %s: %s", timestamp, level, message);
            Platform.runLater(() -> logHandler.accept(logMessage));
        }
    }
    
    /**
     * Logs an informational message.
     * 
     * <p>Used for general server status updates, successful operations,
     * and other informational events during normal server operation.</p>
     * 
     * @param message the informational message to log
     */
    public static void info(String message) {
        addLog("INFO", message);
    }
    
    /**
     * Logs a warning message.
     * 
     * <p>Used for potentially problematic situations that don't prevent
     * normal operation but may require attention, such as retries,
     * recoverable errors, or deprecated usage.</p>
     * 
     * @param message the warning message to log
     */
    public static void warn(String message) {
        addLog("WARN", message);
    }
    
    /**
     * Logs an error message.
     * 
     * <p>Used for error conditions that prevent normal operation,
     * such as failed operations, exceptions, or system failures
     * that require immediate attention.</p>
     * 
     * @param message the error message to log
     */
    public static void error(String message) {
        addLog("ERROR", message);
    }
    
    /**
     * Logs a debug message.
     * 
     * <p>Used for detailed diagnostic information that is typically
     * only of interest during development or troubleshooting. Debug
     * messages provide fine-grained visibility into system behavior.</p>
     * 
     * @param message the debug message to log
     */
    public static void debug(String message) {
        addLog("DEBUG", message);
    }
    
    /**
     * Logs a message at the INFO level.
     * 
     * <p>This is a convenience method that delegates to {@link #info(String)}.
     * Provided for backward compatibility and simplified usage.</p>
     * 
     * @param message the message to log at INFO level
     */
    public static void log(String message) {
        info(message);
    }
}