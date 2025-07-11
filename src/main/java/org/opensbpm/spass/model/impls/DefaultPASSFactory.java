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

    public PASSProcessModel.Builder createPassProcessModel() {
        return DefaultPASSProcessModel.builder();
    }

    public SubjectBehavior.Builder createSubjectBehavior() {
        return DefaultSubjectBehavior.builder();
    }

    public DoState.Builder createDoState() {
        return DefaultDoState.builder();
    }
}
