package it.ludovico.server.service;

import it.ludovico.server.handler.ClientHandler;
import it.ludovico.server.model.ServerModel;
import it.ludovico.server.service.LogService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Network service managing client connections and server lifecycle for the JavaFX Mail System.
 * 
 * <p>This service handles the low-level networking aspects of the mail server, including
 * socket management, client connection acceptance, and thread pool coordination. It provides
 * a clean interface for starting and stopping the server while ensuring proper resource
 * cleanup and graceful shutdown.</p>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Socket Management:</strong> Creates and manages the server socket on port 8888</li>
 *   <li><strong>Connection Handling:</strong> Accepts client connections and delegates to ClientHandler</li>
 *   <li><strong>Thread Pool:</strong> Manages concurrent client connections with a fixed thread pool</li>
 *   <li><strong>Graceful Shutdown:</strong> Ensures proper cleanup of all resources on server stop</li>
 *   <li><strong>Error Handling:</strong> Comprehensive error logging and recovery mechanisms</li>
 * </ul>
 * 
 * <h2>Server Configuration</h2>
 * <ul>
 *   <li><strong>Port:</strong> 8888 (fixed)</li>
 *   <li><strong>Max Clients:</strong> 3 concurrent connections</li>
 *   <li><strong>Protocol:</strong> TCP with ObjectInputStream/ObjectOutputStream</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * <p>This class coordinates multiple threads safely:
 * <ul>
 *   <li>Main server thread for accepting connections</li>
 *   <li>Client thread pool for handling individual requests</li>
 *   <li>Proper synchronization through the ServerModel state</li>
 * </ul></p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 * @see ServerModel
 * @see ClientHandler
 */
public class ServerService {
    /**
     * The TCP port on which the server listens for client connections.
     */
    private static final int PORT = 8888;
    
    /**
     * Maximum number of concurrent client connections supported by the server.
     */
    private static final int MAX_CLIENTS = 3;

    /**
     * Reference to the server model containing business logic and state management.
     */
    private final ServerModel server;
    
    /**
     * The server socket listening for incoming client connections.
     */
    private ServerSocket serverSocket;
    
    /**
     * The main server thread responsible for accepting client connections.
     */
    private Thread serverThread;
    
    /**
     * Thread pool for handling concurrent client requests.
     */
    private ExecutorService clientThreadPool;

    /**
     * Constructs a new ServerService with the specified server model.
     * 
     * <p>The server model provides the business logic and state management
     * capabilities needed by the network service. The service will coordinate
     * with the model to handle client requests and maintain server state.</p>
     * 
     * @param model the server model containing business logic; must not be null
     * @throws NullPointerException if model is null
     */
    public ServerService(ServerModel model) {
        this.server = model;
    }

    /**
     * Starts the mail server and begins accepting client connections.
     * 
     * <p>This method performs the complete server startup sequence:
     * <ol>
     *   <li>Checks if the server is already running (prevents duplicate starts)</li>
     *   <li>Creates a ServerSocket bound to the configured port</li>
     *   <li>Initializes a fixed thread pool for client connections</li>
     *   <li>Updates the server model's running state</li>
     *   <li>Starts the main server thread as a daemon thread</li>
     * </ol></p>
     * 
     * <p>If the server is already running, this method logs a warning and returns
     * without taking any action. If port binding fails, the error is logged and
     * the server remains in a stopped state.</p>
     * 
     * @see #stop()
     * @see #runServer()
     */
    public void start() {
        if(server.getRunning()) {
            LogService.warn("Server is already running");
            return;
        }
        try {
            serverSocket = new ServerSocket(PORT);
            clientThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            server.setRunning(true);

            serverThread = new Thread(this::runServer);
            serverThread.setDaemon(true);
            serverThread.start();
            LogService.info("Server started at port " + PORT);

        } catch (IOException e) {
            LogService.error("Could not listen on port: " + PORT);
        }
    }

    /**
     * Main server loop that accepts and handles client connections.
     * 
     * <p>This method runs continuously while the server is active, accepting
     * incoming client connections and dispatching them to worker threads for
     * processing. Each client connection is handled by a separate {@link ClientHandler}
     * instance running in the thread pool.</p>
     * 
     * <p>The loop continues until the server is stopped, at which point the
     * {@link ServerSocket#accept()} call will throw an IOException due to the
     * socket being closed.</p>
     * 
     * <h3>Connection Processing</h3>
     * <ol>
     *   <li>Accept incoming client socket connection</li>
     *   <li>Create a new ClientHandler for the connection</li>
     *   <li>Submit the handler to the thread pool for execution</li>
     *   <li>Continue accepting new connections</li>
     * </ol>
     * 
     * <p><strong>Note:</strong> This method is designed to run in a separate daemon thread.</p>
     */
    public void runServer() {
        while(server.getRunning()) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                ClientHandler clientHandler = new ClientHandler(
                    clientSocket, 
                    server
                );

                clientThreadPool.submit(clientHandler);
            } catch (IOException e) {
                if (server.getRunning()) {
                    LogService.error("Could not accept client connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Stops the mail server and performs graceful shutdown of all resources.
     * 
     * <p>This method ensures a clean shutdown of the server by following a
     * structured shutdown sequence that properly closes all connections and
     * releases system resources. The shutdown process is designed to give
     * active client connections time to complete before forcing termination.</p>
     * 
     * <h3>Shutdown Sequence</h3>
     * <ol>
     *   <li>Check if server is running (prevents duplicate stops)</li>
     *   <li>Set server running state to false</li>
     *   <li>Close the server socket to stop accepting new connections</li>
     *   <li>Initiate graceful shutdown of the client thread pool</li>
     *   <li>Wait up to 10 seconds for active connections to complete</li>
     *   <li>Force shutdown remaining connections if timeout exceeded</li>
     *   <li>Wait for the main server thread to terminate</li>
     * </ol>
     * 
     * <p>If the server is not running, this method logs a warning and returns
     * without taking any action. All errors during shutdown are logged but do
     * not prevent the shutdown process from continuing.</p>
     * 
     * @see #start()
     */
    public void stop() {
        if (!server.getRunning()) {
            LogService.warn("Server is not running");
            return;
        }

        server.setRunning(false);
        LogService.info("Stopping server...");

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            LogService.error("Error closing server socket: " + e.getMessage());
        }

        if (clientThreadPool != null) {
            clientThreadPool.shutdown();
            try {
                if (!clientThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    LogService.warn("Forcing shutdown of client connections...");
                    clientThreadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                LogService.error("Interrupted during shutdown");
                clientThreadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (serverThread != null) {
            try {
                serverThread.join(5000);
            } catch (InterruptedException e) {
                LogService.error("Interrupted waiting for server thread to finish");
                Thread.currentThread().interrupt();
            }
        }

        LogService.info("Server stopped");
    }
}
