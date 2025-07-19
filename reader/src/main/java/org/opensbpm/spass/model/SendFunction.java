package org.opensbpm.spass.model;

/**
 * @see http://www.i2pm.net/standard-pass-ont#SendFunction
 */
public interface SendFunction extends FunctionSpecification {

    interface Mutable extends SendFunction, FunctionSpecification.Mutable {

    }
}
