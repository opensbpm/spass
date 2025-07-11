package org.opensbpm.spass.model;

/**
 * The main class that contains all relevant process elements
 *
 * @see http://www.i2pm.net/standard-pass-ont#PASSProcessModel
 */
public interface PASSProcessModel extends PASSProcessModelElement {

    interface Mutable extends PASSProcessModel, PASSProcessModelElement.Mutable {

    }

    interface Builder extends PASSProcessModelElement.Builder<PASSProcessModel, Builder> {

    }
}
