package org.opensbpm.spass.model;

/**
 * @see http://www.i2pm.net/standard-pass-ont#CommunicationTransition
 */
public interface CommunicationTransition extends SimplePASSElement,Transition {

    interface Mutable extends CommunicationTransition, Transition.Mutable{

    }

}
