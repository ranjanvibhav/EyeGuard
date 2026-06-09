package com.eyeguard.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to detect if another instance of the application is running
 * using a local loopback port.
 */
public final class SingleInstanceDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceDetector.class);
    private static final int PORT = 49320;
    private static ServerSocket serverSocket;
    private static Thread listenerThread;
    private static volatile boolean running;

    private SingleInstanceDetector() {
        // Prevent instantiation
    }

    /**
     * Tries to register the current instance. If port is already bound,
     * sends a show request to the active instance and returns false.
     *
     * @param onShowRequest callback to execute when a show request is received
     * @return true if this is the only instance, false otherwise
     */
    public static boolean checkAndRegister(final Runnable onShowRequest) {
        try {
            serverSocket = new ServerSocket(PORT, 10, InetAddress.getByName("127.0.0.1"));
            running = true;
            listenerThread = new Thread(() -> startListener(onShowRequest), "eyeguard-instance-listener");
            listenerThread.setDaemon(true);
            listenerThread.start();
            return true;
        } catch (final Exception exception) {
            LOGGER.info("Another instance is running, sending show request...");
            sendShowRequest();
            return false;
        }
    }

    private static void startListener(final Runnable onShowRequest) {
        while (running) {
            try (Socket socket = serverSocket.accept();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                if ("SHOW".equals(reader.readLine())) {
                    onShowRequest.run();
                }
            } catch (final Exception exception) {
                if (running) {
                    LOGGER.debug("Error in single instance listener socket", exception);
                }
            }
        }
    }

    private static void sendShowRequest() {
        try (Socket socket = new Socket("127.0.0.1", PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println("SHOW");
        } catch (final Exception exception) {
            LOGGER.error("Failed to send show request to existing instance", exception);
        }
    }

    /**
     * Closes the server socket and terminates the listener thread.
     */
    public static void shutdown() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (final Exception exception) {
                LOGGER.error("Error closing single instance server socket", exception);
            }
        }
    }
}
