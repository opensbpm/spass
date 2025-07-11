package org.opensbpm.spass;

import org.opensbpm.spass.model.DoState;
import org.opensbpm.spass.model.PASSFactory;
import org.opensbpm.spass.model.PASSProcessModel;
import org.opensbpm.spass.model.SubjectBehavior;
import org.opensbpm.spass.PassOntology.PassOwlDataFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class SPassReader {

    public static PASSProcessModel loadOwl(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {
        return new SPassReader(PassOntology.loadOwl(input)).read(input);
    }

    private final PassOntology passOntology;
    private final PassOwlDataFactory passOwlDataFactory;

    private SPassReader(PassOntology passOntology) {
        this.passOntology = passOntology;
        this.passOwlDataFactory = passOntology.getPassOwlDataFactory();;
    }

    public PASSProcessModel read(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {

        //passOntology.dumpSuperclasses(ontology);
        passOntology.dumpIndividuals();

        OWLNamedIndividual passModelIndividual = passOntology.retrieveNamedIndividualByClass(passOwlDataFactory.getPassProcessModelClass())
                .findFirst()
                .orElseThrow(() -> new SPassIncompleteException("No PASSProcessModel found in ontology"));
        String id = passOntology.readDataProperty(passModelIndividual, "hasModelComponentID");
        String label = passOntology.readDataProperty(passModelIndividual, "hasModelComponentLabel");

        List<SubjectBehavior> subjectBehaviors = passOntology.retrieveContainsOfClass(passModelIndividual, passOwlDataFactory.getSubjectBehaviorClass())
                .map(this::toSubjectBehavior)
                .toList();

        return PASSFactory.getInstance().createPassProcessModel()
                .withId(id)
                .withLabel(label)
                .addContains(subjectBehaviors)
                .build();
    }

    private SubjectBehavior toSubjectBehavior(OWLNamedIndividual subjectBehaviorIndividual) {
        String id = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentID");
        String label = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentLabel");

        List<DoState> doStates = passOntology.retrieveContainsOfClass(subjectBehaviorIndividual, passOwlDataFactory.getDoState())
                .map(this::toDoState)
                .toList();

        return PASSFactory.getInstance().createSubjectBehavior()
                .withId(id)
                .withLabel(label)
                .addContains(doStates)
                .build();
    }

    private DoState toDoState(OWLNamedIndividual subjectBehaviorIndividual) {
        String id = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentID");
        String label = passOntology.readDataProperty(subjectBehaviorIndividual, "hasModelComponentLabel");

        return PASSFactory.getInstance().createDoState()
                .withId(id)
                .withLabel(label)
                .build();
    }

}
