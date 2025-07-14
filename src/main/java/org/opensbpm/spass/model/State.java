package org.opensbpm.spass.model;

public interface State extends BehaviorDescribingComponent {
    /**
     * @see http://www.i2pm.net/standard-pass-ont#hasFunctionSpecification
     */
    FunctionSpecification getHasFunctionSpecification();

    public interface Mutable extends State, BehaviorDescribingComponent.Mutable {

        /**
         * @see http://www.i2pm.net/standard-pass-ont#hasFunctionSpecification
         */
        void setHasFunctionSpecification(FunctionSpecification functionSpecification);

    }

}
