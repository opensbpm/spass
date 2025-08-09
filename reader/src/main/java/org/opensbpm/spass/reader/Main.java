package org.opensbpm.spass.reader;

import org.opensbpm.spass.reader.model.api.*;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws SPassIncompleteException, OWLOntologyCreationException, IOException {
        PASSProcessModel passProcessModel = SPassReader.loadOwl(new File(args[0]));
        System.out.println("Loaded PASS Process Model: " + passProcessModel.getHasModelComponentID());
        StringBuilder pumlDiag = new StringBuilder();
        pumlDiag.append("@startuml\n");
        new VisitContext(new Visitor() {
            @Override
            public void visit(PASSProcessModelElement element) {
                System.out.println("Element ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
            }

            @Override
            public void visitPASSProcessModel(PASSProcessModel element) {
                System.out.println("PASSProcessModel ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
            }

            @Override
            public void visitStartSubject(StartSubject element) {
                System.out.println("StartSubject ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
            }

            @Override
            public void visitSubjectBehavior(SubjectBehavior element) {
                System.out.println("SubjectBehavior ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
            }

            @Override
            public void visitDoState(DoState element) {
                System.out.println("DoState ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
                pumlDiag.append("class ").append(element.getHasModelComponentID()).append(" {\n");
                pumlDiag.append("  label: ").append(element.getHasModelComponentLabel()).append("\n");
                pumlDiag.append("}\n");
            }

            @Override
            public void visitSendState(SendState element) {
                System.out.println("SendState ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
                pumlDiag.append("class ").append(element.getHasModelComponentID()).append(" {\n");
                pumlDiag.append("  label: ").append(element.getHasModelComponentLabel()).append("\n");
                pumlDiag.append("}\n");
            }

            @Override
            public void visitFunctionSpecification(FunctionSpecification element) {
                System.out.println("Function Specification ID: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
            }

            @Override
            public void visitTransition(Transition element) {
                System.out.println("Transition: " + element.getHasModelComponentID() + ", Label: " + element.getHasModelComponentLabel());
                for (State sourceState : element.getHasSourceState()) {
                    for (State targetState : element.getHasTargetState()) {
                        pumlDiag.append(sourceState.getHasModelComponentID()+ "-> " + targetState.getHasModelComponentID() + " : " + element.getHasModelComponentLabel() + "\n");
                    }
                }
            }
        }).visitPASSProcessModel(passProcessModel);
        pumlDiag.append("@enduml\n");
        Path path = Path.of("pumlDiag.puml");
        Files.writeString(path, pumlDiag.toString());
        System.out.println("Generated PlantUML diagram: "+ path.toAbsolutePath().toString());
    }

}
