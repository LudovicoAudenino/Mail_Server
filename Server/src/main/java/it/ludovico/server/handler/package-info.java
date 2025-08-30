/**
 * Client connection handlers for the JavaFX Mail System server.
 * 
 * <p>This package contains the protocol handlers responsible for processing
 * individual client requests and implementing the mail system's communication
 * protocol. Each handler manages a single client connection and processes
 * commands according to the stateless HTTP-like protocol design.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.server.handler.ClientHandler} - Individual client request processor</li>
 * </ul>
 * 
 * <h2>Protocol Implementation</h2>
 * <p>The handlers implement a comprehensive mail protocol supporting:</p>
 * <ul>
 *   <li><strong>Authentication:</strong> LOGIN command with user validation</li>
 *   <li><strong>Email Operations:</strong> SEND_EMAIL, REPLY_EMAIL, REPLY_ALL_EMAIL, FORWARD_EMAIL</li>
 *   <li><strong>Mailbox Management:</strong> FETCH_NEW_EMAIL, DELETE_EMAIL</li>
 *   <li><strong>Validation:</strong> CHECK_EMAIL_EXISTS for address verification</li>
 *   <li><strong>Monitoring:</strong> PING command for connectivity testing</li>
 * </ul>
 * 
 * <h2>Connection Lifecycle</h2>
 * <ol>
 *   <li>Client connects to server on port 8888</li>
 *   <li>Server creates ClientHandler instance in thread pool</li>
 *   <li>Handler reads command from client via ObjectInputStream</li>
 *   <li>Handler processes command through EmailService</li>
 *   <li>Handler sends response via ObjectOutputStream</li>
 *   <li>Connection is closed automatically (stateless design)</li>
 * </ol>
 * 
 * <h2>Threading</h2>
 * <p>Each ClientHandler runs in its own thread from the server's thread pool,
 * allowing concurrent processing of multiple client requests while maintaining
 * thread safety through the underlying EmailService implementation.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.server.handler;