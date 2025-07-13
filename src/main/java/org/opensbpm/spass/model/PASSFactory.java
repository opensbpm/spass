package org.opensbpm.spass.model;

import org.opensbpm.spass.model.impls.DefaultPASSFactory;

public interface PASSFactory {

    static PASSFactory getInstance() {
        return DefaultPASSFactory.getInstance();
    }

    PASSProcessModel.Mutable createPASSProcessModel();

    SubjectBehavior.Mutable createSubjectBehavior();

    DoState.Mutable createDoState();
}
