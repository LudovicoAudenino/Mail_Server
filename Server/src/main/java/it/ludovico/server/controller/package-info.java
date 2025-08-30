/**
 * JavaFX UI controllers for the mail server interface.
 * 
 * <p>This package contains the JavaFX controllers that manage the server's
 * graphical user interface. The controllers provide real-time monitoring
 * capabilities, server control functions, and live logging display for
 * server administrators.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.server.controller.ServerController} - Main server UI controller</li>
 * </ul>
 * 
 * <h2>UI Features</h2>
 * <ul>
 *   <li><strong>Server Control:</strong> Start/stop server functionality with visual feedback</li>
 *   <li><strong>Status Monitoring:</strong> Real-time display of server running status</li>
 *   <li><strong>Request Tracking:</strong> Live counter of processed client requests</li>
 *   <li><strong>Log Display:</strong> Auto-updating log viewer with scroll-to-bottom</li>
 *   <li><strong>Dynamic UI:</strong> Button states and colors change based on server status</li>
 * </ul>
 * 
 * <h2>JavaFX Integration</h2>
 * <p>The controllers leverage JavaFX's property binding system to create
 * responsive interfaces that automatically update when server state changes:</p>
 * <ul>
 *   <li><strong>Property Bindings:</strong> Automatic UI updates without manual refresh</li>
 *   <li><strong>Observable Collections:</strong> Real-time log display updates</li>
 *   <li><strong>Thread Safety:</strong> Proper Platform.runLater() usage for UI updates</li>
 * </ul>
 * 
 * <h2>FXML Integration</h2>
 * <p>Controllers are designed to work with FXML layout files, following JavaFX
 * best practices for separation of UI design and controller logic.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.server.controller;