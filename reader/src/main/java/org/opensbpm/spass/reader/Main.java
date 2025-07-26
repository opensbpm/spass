package org.opensbpm.spass.reader;

import org.opensbpm.spass.reader.model.api.DoState;
import org.opensbpm.spass.reader.model.api.FunctionSpecification;
import org.opensbpm.spass.reader.model.api.PASSProcessModel;
import org.opensbpm.spass.reader.model.api.PASSProcessModelElement;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;

public class Main {

    public static void main(String[] args) throws SPassIncompleteException, OWLOntologyCreationException {
        PASSProcessModel passProcessModel = SPassReader.loadOwl(new File(args[0]));
        System.out.println("Loaded PASS Process Model: " + passProcessModel.getHasModelComponentID());
        visit(passProcessModel);
    }

    private static void visit(PASSProcessModelElement element) {
        if (element instanceof DoState) {
            visitDoState((DoState) element);
        } else {
            System.out.println("Element ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
        }
        if (element.getContains() != null) {
            element.getContains().forEach(Main::visit);
        }
    }

    private static void visitDoState(DoState element) {
        System.out.println("DoState ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
        if (element.getHasFunctionSpecification() != null) {
            element.getHasFunctionSpecification().forEach(Main::visitFunctionSpecification);
        }
    }

    private static void visitFunctionSpecification(FunctionSpecification element) {
        System.out.println("Function Specification ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
    }
}
