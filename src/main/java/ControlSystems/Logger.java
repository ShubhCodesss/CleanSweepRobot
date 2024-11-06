package ControlSystems;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * Logger class to handle logging of Clean Sweep's actions.
 */
public class Logger {
    private PrintWriter writer;

    /**
     * Initializes the logger with the specified log file.
     * @param filename Name of the log file.
     * @throws IOException If the file cannot be opened.
     */
    public Logger(String filename) throws IOException {
        writer = new PrintWriter(new FileWriter(filename, true));
    }

    /**
     * Logs a message with a timestamp.
     * @param message The message to log.
     */
    public void log(String message) {
        writer.println(LocalDateTime.now() + " - " + message);
        writer.flush();
    }

    /**
     * Closes the logger.
     */
    public void close() {
        writer.close();
    }
}
