package org.opensbpm.spass.model;

/**
 * This class is the super class of all model elements used to define or specify the interaction means within a process model
 *
 * @see http://www.i2pm.net/standard-pass-ont#InteractionDescribingComponent
 */
public interface InteractionDescribingComponent extends PASSProcessModelElement {

    interface Mutable extends InteractionDescribingComponent, PASSProcessModelElement.Mutable {

    }
}
