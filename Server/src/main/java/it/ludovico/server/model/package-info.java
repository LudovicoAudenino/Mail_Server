/**
 * Server-side business logic and data models for the JavaFX Mail System.
 * 
 * <p>This package contains the core business logic implementation for the mail server,
 * including the primary server model that handles email operations, user management,
 * and server state coordination. The classes in this package implement the email
 * service contracts and provide thread-safe operations for concurrent client access.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.server.model.ServerModel} - Main business logic implementation</li>
 * </ul>
 * 
 * <h2>Features</h2>
 * <ul>
 *   <li><strong>Email Management:</strong> Send, receive, delete, and organize emails</li>
 *   <li><strong>User Authentication:</strong> Validate registered accounts</li>
 *   <li><strong>Delivery Tracking:</strong> Prevent duplicate email delivery</li>
 *   <li><strong>Server Monitoring:</strong> Real-time status and request tracking</li>
 *   <li><strong>Thread Safety:</strong> Safe concurrent access for multiple clients</li>
 * </ul>
 * 
 * <h2>Design Patterns</h2>
 * <ul>
 *   <li><strong>Service Pattern:</strong> ServerModel implements EmailService interface</li>
 *   <li><strong>Observer Pattern:</strong> JavaFX properties for UI binding</li>
 *   <li><strong>Repository Pattern:</strong> Delegates persistence to repository layer</li>
 * </ul>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.server.model;