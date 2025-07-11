package org.opensbpm.spass.model;

import org.opensbpm.spass.model.impls.DefaultPASSFactory;

public interface PASSFactory {

    public static PASSFactory getInstance() {
        return DefaultPASSFactory.getInstance();
    }

    PASSProcessModel.Builder createPassProcessModelBuilder();
    PASSProcessModel.Mutable createPASSProcessModel();

    SubjectBehavior.Builder createSubjectBehaviorBuilder();

    SubjectBehavior.Mutable createSubjectBehavior();

    DoState.Builder createDoStateBuilder();

    DoState.Mutable createDoState();
}
