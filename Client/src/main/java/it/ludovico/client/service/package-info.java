/**
 * Network services and utilities for the JavaFX Mail System client.
 * 
 * <p>This package provides the service layer for the mail client, handling
 * network communication with the server, user notifications, and background
 * operations. The services coordinate between the UI controllers and the
 * mail server while providing proper error handling and user feedback.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.client.service.ClientService} - Main network communication service</li>
 *   <li>{@link it.ludovico.client.service.AlertService} - User notification and alert system</li>
 * </ul>
 * 
 * <h2>Service Responsibilities</h2>
 * <ul>
 *   <li><strong>Network Communication:</strong> TCP socket connections to mail server</li>
 *   <li><strong>Protocol Implementation:</strong> Client-side mail protocol handling</li>
 *   <li><strong>Background Operations:</strong> Asynchronous email operations with threading</li>
 *   <li><strong>Auto-refresh:</strong> Periodic checking for new emails</li>
 *   <li><strong>Server Monitoring:</strong> Heartbeat system for server availability</li>
 *   <li><strong>User Notifications:</strong> Success, error, and status alerts</li>
 * </ul>
 * 
 * <h2>Threading Architecture</h2>
 * <p>Services implement comprehensive threading strategies:</p>
 * <ul>
 *   <li><strong>Background Threads:</strong> Network operations don't block UI</li>
 *   <li><strong>Scheduled Tasks:</strong> Auto-refresh and heartbeat monitoring</li>
 *   <li><strong>JavaFX Integration:</strong> Platform.runLater() for UI updates</li>
 *   <li><strong>Resource Management:</strong> Proper cleanup of threads and connections</li>
 * </ul>
 * 
 * <h2>Error Handling</h2>
 * <p>Services provide comprehensive error handling with user-friendly feedback:</p>
 * <ul>
 *   <li><strong>Network Errors:</strong> Connection failure detection and reporting</li>
 *   <li><strong>Server Errors:</strong> Server response error handling</li>
 *   <li><strong>User Alerts:</strong> Informative error messages with suggested actions</li>
 * </ul>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.client.service;