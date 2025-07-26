package org.opensbpm.spass.reader;

import org.opensbpm.spass.reader.model.api.DoState;
import org.opensbpm.spass.reader.model.api.FunctionSpecification;
import org.opensbpm.spass.reader.model.api.PASSProcessModelElement;

class VisitContext {
    private final Visitor visitor;

    public VisitContext(Visitor visitor) {
        this.visitor = visitor;
    }

    public void visit(PASSProcessModelElement element) {
        if (element instanceof DoState) {
            visitDoState((DoState) element);
        } else {
            visitor.visit(element);
        }
        if (element.getContains() != null) {
            element.getContains().forEach(this::visit);
        }
    }

    private void visitDoState(DoState element) {
        visitor.visitDoState(element);
        if (element.getHasFunctionSpecification() != null) {
            element.getHasFunctionSpecification().forEach(this::visitFunctionSpecification);
        }
    }

    private void visitFunctionSpecification(FunctionSpecification element) {
        visitor.visitFunctionSpecification(element);
    }
}
