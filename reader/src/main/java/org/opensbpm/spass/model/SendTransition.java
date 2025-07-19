package org.opensbpm.spass.model;

/**
 * @see http://www.i2pm.net/standard-pass-ont#SendTransition
 */
public interface SendTransition extends CommunicationTransition {

    interface Mutable extends SendTransition, CommunicationTransition.Mutable {

    }

}
