package org.opensbpm.spass;

import org.opensbpm.spass.model.*;
import org.opensbpm.spass.model.PASSProcessModelElement.Mutable;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import static org.opensbpm.spass.OwlUtils.createLocalIRIMapper;

public class SPassReader {

    /**
     * Loads a PASSProcessModel from an PASS OWL ontology file.
     *
     * @param passFile File containing the PASS OWL ontology.
     * @return A PASSProcessModel instance populated with data from the ontology.
     * @throws SPassIncompleteException     If the ontology does not contain a complete PASSProcessModel.
     * @throws OWLOntologyCreationException If there is an error creating the ontology from the input stream.
     */
    public static PASSProcessModel loadOwl(File passFile) throws SPassIncompleteException, OWLOntologyCreationException {
        return loadOwl(new FileDocumentSource(passFile));
    }

    /**
     * Loads a PASSProcessModel from an PASS OWL ontology input stream.
     *
     * @param passInputStream InputStream containing the PASS OWL ontology.
     * @return A PASSProcessModel instance populated with data from the ontology.
     * @throws SPassIncompleteException     If the ontology does not contain a complete PASSProcessModel.
     * @throws OWLOntologyCreationException If there is an error creating the ontology from the input stream.
     */
    public static PASSProcessModel loadOwl(InputStream passInputStream) throws SPassIncompleteException, OWLOntologyCreationException {
        return loadOwl(new StreamDocumentSource(passInputStream));
    }

    private static PASSProcessModel loadOwl(OWLOntologyDocumentSource source) throws SPassIncompleteException, OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        // Add custom IRI mapper to redirect external IRI to local file
        manager.setIRIMappers(Set.of(
                createLocalIRIMapper("http://www.i2pm.net/standard-pass-ont", "/standard_PASS_ont_dev.owl")
        ));
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(source, config);

        OwlUtils.dumpIndividuals(ontology);

        Map<OWLNamedIndividual, Mutable> modelElements = ontology.individualsInSignature()
                .map(namedIndividual -> {
                    OWLClass owlClass = OwlUtils.getTypeOfIndividual(ontology, namedIndividual);
                    Mutable modelElement = ModelUtils.instantiateClass(owlClass);
                    return Pair.of(namedIndividual, modelElement);
                })
                .collect(Pair.toMap());

        ontology.axioms(AxiomType.DATA_PROPERTY_ASSERTION)
                .forEach(dataPropertyAssertion -> {
                    IRI propertyIRI = dataPropertyAssertion.getProperty().asOWLDataProperty().getIRI();
                    OWLNamedIndividual subject = dataPropertyAssertion.getSubject().asOWLNamedIndividual();
                    ModelUtils.consumeProperty(propertyIRI, modelElements.get(subject), dataPropertyAssertion.getObject());
                });

        ontology.axioms(AxiomType.OBJECT_PROPERTY_ASSERTION)
                .forEach(objectPropertyAssertion -> {
                    IRI propertyIRI = objectPropertyAssertion.getProperty().asOWLObjectProperty().getIRI();
                    OWLNamedIndividual subject = objectPropertyAssertion.getSubject().asOWLNamedIndividual();
                    OWLNamedIndividual object = objectPropertyAssertion.getObject().asOWLNamedIndividual();
                    ModelUtils.consumeObject(propertyIRI, modelElements.get(subject), modelElements.get(object));
                });

        return modelElements.values().stream()
                .filter(element -> element instanceof PASSProcessModel.Mutable)
                .map(element -> (PASSProcessModel.Mutable) element)
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple PASSProcessModel found in ontology");
                })
                .orElseThrow(() -> new SPassIncompleteException("No PASSProcessModel found in ontology"));
    }

}
