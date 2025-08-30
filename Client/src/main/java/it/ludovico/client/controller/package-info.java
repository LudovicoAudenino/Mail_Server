/**
 * JavaFX UI controllers for the mail client interface.
 * 
 * <p>This package contains the JavaFX controllers that manage the client's
 * graphical user interface. The controllers handle user interactions, coordinate
 * with services for data operations, and provide a comprehensive email client
 * experience with login, mailbox management, and email composition capabilities.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.client.controller.NavigationController} - Scene navigation management</li>
 *   <li>{@link it.ludovico.client.controller.LoginController} - User authentication interface</li>
 *   <li>{@link it.ludovico.client.controller.MailboxController} - Main email inbox interface</li>
 *   <li>{@link it.ludovico.client.controller.ComposeEmailController} - Email composition interface</li>
 * </ul>
 * 
 * <h2>UI Features</h2>
 * <ul>
 *   <li><strong>Navigation:</strong> Seamless scene transitions between application views</li>
 *   <li><strong>Authentication:</strong> User login with server validation</li>
 *   <li><strong>Email Management:</strong> View, delete, reply, and forward emails</li>
 *   <li><strong>Composition:</strong> Create and send new emails with recipient validation</li>
 *   <li><strong>Real-time Updates:</strong> Auto-refresh for new emails and server status</li>
 * </ul>
 * 
 * <h2>Controller Architecture</h2>
 * <p>Controllers follow JavaFX best practices and MVC principles:</p>
 * <ul>
 *   <li><strong>FXML Integration:</strong> Controllers bound to FXML layout files</li>
 *   <li><strong>Service Coordination:</strong> Delegate business logic to service layer</li>
 *   <li><strong>Model Binding:</strong> Use observable properties for automatic UI updates</li>
 *   <li><strong>Event Handling:</strong> Process user interactions and system events</li>
 * </ul>
 * 
 * <h2>User Experience</h2>
 * <p>Controllers provide a modern, responsive email client experience:</p>
 * <ul>
 *   <li><strong>Intuitive Interface:</strong> Familiar email client layout and workflow</li>
 *   <li><strong>Status Indicators:</strong> Connection status and operation feedback</li>
 *   <li><strong>Error Handling:</strong> User-friendly error messages and recovery options</li>
 *   <li><strong>Keyboard Shortcuts:</strong> Efficient navigation and operations</li>
 * </ul>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.client.controller;