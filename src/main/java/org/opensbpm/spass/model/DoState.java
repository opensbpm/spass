package org.opensbpm.spass.model;

/**
 * The standard state in a PASS subject behavior diagram denoting an action or activity of the subject in itself
 * @see http://www.i2pm.net/standard-pass-ont#DoState
 */
public interface DoState extends StandardPASSState {

    interface Mutable extends DoState, StandardPASSState.Mutable {

    }

    interface Builder extends PASSProcessModelElement.Builder<DoState, Builder> {

    }
}
