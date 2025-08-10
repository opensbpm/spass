package org.opensbpm.spass.reader;

import org.opensbpm.spass.reader.model.api.*;

class VisitContext {
    private final Visitor visitor;

    public VisitContext(Visitor visitor) {
        this.visitor = visitor;
    }

    public void visit(PASSProcessModelElement element) {
        if (element instanceof State) {
            visitState((State) element);
        } else {
            visitor.visitPASSProcessModelElement(element);
        }
        if (element.getContains() != null) {
            element.getContains().forEach(this::visit);
        }
    }

    public void visitPASSProcessModel(PASSProcessModel element) {
        visitor.visitPASSProcessModel(element);
        if (element.getHasStartSubject() != null) {
            element.getHasStartSubject().forEach(this::visitStartSubject);
        }
    }

    private void visitStartSubject(StartSubject element) {
        visitor.visitStartSubject(element);
        if (element.getContainsBehavior() != null) {
            element.getContainsBehavior().forEach(this::visitSubjectBehavior);
        }

    }

    private void visitSubjectBehavior(SubjectBehavior element) {
        visitor.visitSubjectBehavior(element);
        if (element.getContains() != null) {
            element.getContains().forEach(this::visit);
        }
    }

    private void visitState(State element) {
        if (element instanceof DoState) {
            visitDoState((DoState) element);
        } else if (element instanceof SendState) {
            visitSendState((SendState) element);
        } else {
            throw new UnsupportedOperationException("Unsupported state type: " + element.getClass().getName());
        }
        if (element.getHasFunctionSpecification() != null) {
            element.getHasFunctionSpecification().forEach(this::visitFunctionSpecification);
        }

        if (element.getHasOutgoingTransition() != null) {
            element.getHasOutgoingTransition().forEach(this::visitTransition);
        }
    }

    private void visitDoState(DoState element) {
        visitor.visitDoState(element);
    }

    private void visitSendState(SendState element) {
        visitor.visitSendState(element);
    }

    private void visitTransition(Transition element) {
        visitor.visitTransition(element);
        if (element.getHasTargetState() != null) {
            element.getHasTargetState().forEach(this::visitState);
        }
    }

    private void visitFunctionSpecification(FunctionSpecification element) {
        visitor.visitFunctionSpecification(element);
    }
}
