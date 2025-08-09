package org.opensbpm.spass.reader;

import org.opensbpm.spass.reader.model.api.*;

public interface Visitor {

    void visit(PASSProcessModelElement element);

    void visitPASSProcessModel(PASSProcessModel element);

    void visitStartSubject(StartSubject element);

    void visitSubjectBehavior(SubjectBehavior element);

    void visitDoState(DoState element);

    void visitSendState(SendState element);

    void visitFunctionSpecification(FunctionSpecification element);

    void visitTransition(Transition element);

}
