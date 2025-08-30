/**
 * Shared data models and communication protocol definitions.
 * 
 * <p>This package contains the core data models and protocol constants used
 * for communication between the mail client and server. These classes ensure
 * consistent data representation and protocol implementation across the
 * distributed mail system.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.shared.model.Email} - Email message data model</li>
 *   <li>{@link it.ludovico.shared.model.Commands} - Communication protocol constants</li>
 * </ul>
 * 
 * <h2>Email Data Model</h2>
 * <p>The {@link it.ludovico.shared.model.Email} class provides:</p>
 * <ul>
 *   <li><strong>Message Content:</strong> Sender, recipients, subject, and body</li>
 *   <li><strong>Metadata:</strong> Unique ID, timestamp, and delivery tracking</li>
 *   <li><strong>Serialization:</strong> Network transmission support</li>
 *   <li><strong>Delivery Tracking:</strong> Per-user delivery status for synchronization</li>
 * </ul>
 * 
 * <h2>Communication Protocol</h2>
 * <p>The {@link it.ludovico.shared.model.Commands} class defines:</p>
 * <ul>
 *   <li><strong>Authentication:</strong> LOGIN command for user validation</li>
 *   <li><strong>Email Operations:</strong> SEND, REPLY, FORWARD commands</li>
 *   <li><strong>Mailbox Management:</strong> FETCH, DELETE commands</li>
 *   <li><strong>Utilities:</strong> PING, CHECK_EMAIL_EXISTS commands</li>
 * </ul>
 * 
 * <h2>Protocol Design</h2>
 * <p>The communication follows a stateless, HTTP-like pattern:</p>
 * <ol>
 *   <li>Client opens TCP connection to server</li>
 *   <li>Client sends command string and parameters</li>
 *   <li>Server processes request and returns response</li>
 *   <li>Connection is closed immediately</li>
 * </ol>
 * 
 * <h2>Serialization</h2>
 * <p>All shared models implement {@link java.io.Serializable} for network
 * transmission using Java's ObjectInputStream/ObjectOutputStream.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.shared.model;