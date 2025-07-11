package org.opensbpm.spass.model;

import org.opensbpm.spass.model.impls.DefaultPASSFactory;

public interface PASSFactory {

    public static PASSFactory getInstance() {
        return DefaultPASSFactory.getInstance();
    }

    PASSProcessModel.Builder createPassProcessModel();

    SubjectBehavior.Builder createSubjectBehavior();

    DoState.Builder createDoState();
}
