package org.opensbpm.spass;

import org.opensbpm.spass.model.*;
import org.semanticweb.owlapi.model.*;

import java.io.InputStream;
import java.util.*;

public class SPassReader {

    public static PASSProcessModel loadOwl(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {
        return new SPassReader(PassOntology.loadOwl(input)).read(input);
    }

    private final PassOntology passOntology;

    private SPassReader(PassOntology passOntology) {
        this.passOntology = passOntology;
    }

    public PASSProcessModel read(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {

        //passOntology.dumpSuperclasses(ontology);
        //passOntology.dumpIndividuals();
        //passOntology.listObjectPropertiesWithDomainAndRange();

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
                    ModelUtils.invoke(propertyIRI, modelElements.get(subject), dataPropertyAssertion.getObject());
                });

        passOntology.getObjectPropertyAssertions()
                .forEach(objectPropertyAssertion -> {
                    IRI propertyIRI = objectPropertyAssertion.getProperty().asOWLObjectProperty().getIRI();
                    OWLNamedIndividual subject = objectPropertyAssertion.getSubject().asOWLNamedIndividual();
                    OWLNamedIndividual object = objectPropertyAssertion.getObject().asOWLNamedIndividual();
                    ModelUtils.invoke2(propertyIRI, modelElements.get(subject), modelElements.get(object));
                });


        return modelElements.values().stream()
                .filter(element -> element instanceof PASSProcessModel.Mutable)
                .map(element -> (PASSProcessModel.Mutable) element)
                .findFirst()
                .orElseThrow(() -> new SPassIncompleteException("No PASSProcessModel found in ontology"));
    }
}
