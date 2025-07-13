package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.DoState;
import org.opensbpm.spass.model.PASSFactory;
import org.opensbpm.spass.model.PASSProcessModel;
import org.opensbpm.spass.model.SubjectBehavior;

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
}
