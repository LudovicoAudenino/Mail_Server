/**
 * Network services and utilities for the JavaFX Mail System server.
 * 
 * <p>This package provides the service layer for the mail server, including
 * network communication, logging, and service interface definitions. It handles
 * the coordination between the server infrastructure and business logic layers.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.server.service.ServerService} - Network connection management</li>
 *   <li>{@link it.ludovico.server.service.LogService} - Centralized logging system</li>
 *   <li>{@link it.ludovico.server.service.EmailService} - Business logic interface</li>
 * </ul>
 * 
 * <h2>Service Responsibilities</h2>
 * <ul>
 *   <li><strong>Network Management:</strong> TCP socket handling and client connections</li>
 *   <li><strong>Thread Coordination:</strong> Multi-threaded client request processing</li>
 *   <li><strong>Logging:</strong> Centralized, thread-safe logging with UI integration</li>
 *   <li><strong>Resource Cleanup:</strong> Graceful shutdown and resource management</li>
 * </ul>
 * 
 * <h2>Architecture</h2>
 * <ul>
 *   <li><strong>Server Service:</strong> Manages server lifecycle and client connections</li>
 *   <li><strong>Log Service:</strong> Static utility for application-wide logging</li>
 *   <li><strong>Email Service:</strong> Interface contract for business logic operations</li>
 * </ul>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.server.service;