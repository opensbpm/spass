package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.FunctionSpecification;
import org.opensbpm.spass.model.State;

public class MutableState extends MutablePASSProcessModelElement implements State.Mutable {
    private FunctionSpecification functionSpecification;

    @Override
    public void setHasFunctionSpecification(FunctionSpecification functionSpecification) {
        this.functionSpecification = functionSpecification;
    }

    @Override
    public FunctionSpecification getHasFunctionSpecification() {
        return functionSpecification;
    }
}
