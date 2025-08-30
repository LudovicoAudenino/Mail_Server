/**
 * Shared components used by both client and server in the JavaFX Mail System.
 * 
 * <p>This package contains classes and interfaces that are common to both the
 * client and server applications. These shared components ensure consistency
 * in data models and communication protocols across the distributed system.</p>
 * 
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>{@link it.ludovico.shared.model} - Shared data models and protocol definitions</li>
 * </ul>
 * 
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><strong>Code Reuse:</strong> Eliminate duplication between client and server</li>
 *   <li><strong>Protocol Consistency:</strong> Ensure identical communication protocols</li>
 *   <li><strong>Data Integrity:</strong> Maintain consistent data models across tiers</li>
 *   <li><strong>Serialization:</strong> Support network transmission of objects</li>
 * </ul>
 * 
 * <h2>Deployment</h2>
 * <p>Shared classes are duplicated in both client and server projects to maintain
 * project independence while ensuring consistency. Changes to shared components
 * should be synchronized across both projects.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
package it.ludovico.shared;