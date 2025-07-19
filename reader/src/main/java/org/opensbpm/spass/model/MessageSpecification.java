package org.opensbpm.spass.model;

/**
 * MessageSpecification are model elements that specify the existence of a message. At minimum its name and id.
 * <p>
 * It may contain additional specification for its payload (contained Data, exact form etc.)
 *
 * @see http://www.i2pm.net/standard-pass-ont#MessageSpecification
 */
public interface MessageSpecification extends InteractionDescribingComponent,SimplePASSElement {

    interface Mutable extends MessageSpecification, InteractionDescribingComponent.Mutable {

    }
}
