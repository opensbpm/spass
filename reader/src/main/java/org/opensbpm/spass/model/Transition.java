package org.opensbpm.spass.model;

/**
 * @see http://www.i2pm.net/standard-pass-ont#Transition
 */
public interface Transition extends BehaviorDescribingComponent{

    interface Mutable extends Transition, BehaviorDescribingComponent.Mutable{

    }

}
