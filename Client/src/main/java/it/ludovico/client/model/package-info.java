/**
 * Client-side data models and state management for the JavaFX Mail System.
 * 
 * <p>This package contains the data models and state management classes for the
 * mail client application. The classes provide observable properties for seamless
 * JavaFX UI binding and maintain the client's local state including user information,
 * email collections, and connection status.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link it.ludovico.client.model.ClientModel} - Primary client state and data container</li>
 * </ul>
 * 
 * <h2>Features</h2>
 * <ul>
 *   <li><strong>User Management:</strong> Store and manage current user identity</li>
 *   <li><strong>Email Storage:</strong> Observable collections for automatic UI synchronization</li>
 *   <li><strong>Connection Status:</strong> Track and report server connectivity state</li>
 *   <li><strong>JavaFX Integration:</strong> Properties and observable collections for UI binding</li>
 *   <li><strong>State Persistence:</strong> Maintain client state across operations</li>
 * </ul>
 * 
 * <h2>Observable Properties</h2>
 * <p>The model classes provide JavaFX observable properties that enable:</p>
 * <ul>
 *   <li><strong>Automatic UI Updates:</strong> UI components automatically reflect model changes</li>
 *   <li><strong>Real-time Synchronization:</strong> Changes immediately visible across the interface</li>
 *   <li><strong>Event-driven Architecture:</strong> Listeners can respond to state changes</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * <p>Model classes are designed to work safely with JavaFX's threading model,
 * ensuring proper UI updates via Platform.runLater() when modified from
 * background threads.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.client.model;