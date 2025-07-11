package org.opensbpm.spass;

import org.opensbpm.spass.model.*;
import org.opensbpm.spass.PassOntology.PassOwlDataFactory;
import org.semanticweb.owlapi.model.*;

import java.io.InputStream;
import java.util.*;

import static java.lang.String.format;

public class SPassReader {

    public static PASSProcessModel loadOwl(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {
        return new SPassReader(PassOntology.loadOwl(input)).read(input);
    }

    private final PassOntology passOntology;
    private final PassOwlDataFactory passOwlDataFactory;

    private SPassReader(PassOntology passOntology) {
        this.passOntology = passOntology;
        this.passOwlDataFactory = passOntology.getPassOwlDataFactory();
    }

    public PASSProcessModel read(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {

        //passOntology.dumpSuperclasses(ontology);
        passOntology.dumpIndividuals();
        passOntology.listObjectPropertiesWithDomainAndRange();

        Map<OWLNamedIndividual, PASSProcessModelElement.Mutable> modelElements = passOntology.getIndividuals()
                .map(namedIndividual -> {
                    OWLClass owlClass = passOntology.getTypeOfIndividual(namedIndividual);
                    PASSProcessModelElement.Mutable modelElement = ModelUtils.instantiate(owlClass);
                    return Pair.of(namedIndividual, modelElement);
                })
                .collect(Pair.toMap());

        passOntology.getDataPropertyAssertions()
                .forEach(dataPropertyAssertion -> {
                    IRI propertyIRI = dataPropertyAssertion.getProperty().asOWLDataProperty().getIRI();
                    OWLNamedIndividual subject = dataPropertyAssertion.getSubject().asOWLNamedIndividual();
                    ModelUtils.invoke(propertyIRI,modelElements.get(subject),dataPropertyAssertion.getObject());
                });

        // Print data property assertions
        passOntology.getDataPropertyAssertions()
                .forEach(dataPropertyAssertion -> {
                    System.out.println(format("Data Property: %s, Subject: %s, Object: %s",
                            dataPropertyAssertion.getProperty().asOWLDataProperty().getIRI(),
                            dataPropertyAssertion.getSubject().asOWLNamedIndividual().getIRI(),
                            dataPropertyAssertion.getObject()));
                });


        OWLNamedIndividual passModelIndividual = passOntology.retrieveNamedIndividualByClass(passOwlDataFactory.getPassProcessModelClass())
                .findFirst()
                .orElseThrow(() -> new SPassIncompleteException("No PASSProcessModel found in ontology"));
        String id = passOntology.readDataProperty(passModelIndividual, "hasModelComponentID");
        String label = passOntology.readDataProperty(passModelIndividual, "hasModelComponentLabel");

        List<SubjectBehavior> subjectBehaviors = passOntology.retrieveContainsOfClass(passModelIndividual, passOwlDataFactory.getSubjectBehaviorClass())
                .map(this::toSubjectBehavior)
                .toList();

        return modelElements.values()                .stream()
                .filter(element -> element instanceof PASSProcessModel.Mutable)
                .map(element -> (PASSProcessModel.Mutable) element)
                .findFirst()
                .orElseThrow(() -> new SPassIncompleteException("No PASSProcessModel found in ontology"));
    }

    private SubjectBehavior toSubjectBehavior(OWLNamedIndividual subjectBehaviorIndividual) {
        String id = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentID");
        String label = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentLabel");

        List<DoState> doStates = passOntology.retrieveContainsOfClass(subjectBehaviorIndividual, passOwlDataFactory.getDoState())
                .map(this::toDoState)
                .toList();

        return PASSFactory.getInstance().createSubjectBehavior();
    }

    private DoState toDoState(OWLNamedIndividual subjectBehaviorIndividual) {
        String id = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentID");
        String label = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentLabel");

        return PASSFactory.getInstance().createDoState();
    }

}
