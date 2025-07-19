package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.*;

import java.util.Collection;
import java.util.List;

public class DefaultPASSFactory implements PASSFactory {
    private final static DefaultPASSFactory INSTANCE = new DefaultPASSFactory();

    public static PASSFactory getInstance() {
        return INSTANCE;
    }

    private DefaultPASSFactory() {
        // no instance creation
    }

    @Override
    public PASSProcessModel.Mutable createPASSProcessModel() {
        return new MutablePASSProcessModel();
    }

    @Override
    public SubjectBehavior.Mutable createSubjectBehavior() {
        return new MutableSubjectBehavior();
    }

    @Override
    public DoState.Mutable createDoState() {
        return new MutableDoState();
    }

    @Override
    public DoFunction.Mutable createDoFunction() {
        return new MutableDoFunction();
    }

    @Override
    public SendState.Mutable createSendState() {
        return new MutableSendState();
    }

    @Override
    public SendFunction.Mutable createSendFunction() {
        return new MutableSendFunction();
    }

    @Override
    public MessageSpecification.Mutable createMessageSpecification() {
        return new MutableMessageSpecification();
    }
    @Override
    public SendTransition.Mutable createSendTransition() {
        return new MutableSendTransition();
    }
}
