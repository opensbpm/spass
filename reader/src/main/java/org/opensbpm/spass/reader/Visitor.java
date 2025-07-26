package org.opensbpm.spass.reader;

import org.opensbpm.spass.reader.model.api.DoState;
import org.opensbpm.spass.reader.model.api.FunctionSpecification;
import org.opensbpm.spass.reader.model.api.PASSProcessModelElement;

public interface Visitor {
    void visit(PASSProcessModelElement element);

    void visitDoState(DoState element);

    void visitFunctionSpecification(FunctionSpecification element);
}
