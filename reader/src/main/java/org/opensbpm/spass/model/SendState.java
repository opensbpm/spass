package org.opensbpm.spass.model;

/**
 * The standard state in a PASS subject behavior diagram denoting a send action
 * @see http://www.i2pm.net/standard-pass-ont#SendState
 */
public interface SendState extends StandardPASSState {

    interface Mutable extends SendState, StandardPASSState.Mutable {

    }
}
