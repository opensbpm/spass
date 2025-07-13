package org.opensbpm.spass;

import org.opensbpm.spass.model.*;
import org.opensbpm.spass.model.PASSProcessModelElement.Mutable;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;
import java.io.InputStream;
import java.util.*;
import static org.opensbpm.spass.OwlUtils.createLocalIRIMapper;

public class SPassReader {

    public static PASSProcessModel loadOwl(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {
        OWLOntology ontology = loadPassOntology(input);

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

    private static OWLOntology loadPassOntology(InputStream input) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        // Add custom IRI mapper to redirect external IRI to local file
        manager.setIRIMappers(Set.of(
                createLocalIRIMapper("http://www.i2pm.net/standard-pass-ont", "/standard_PASS_ont_dev.owl")
        ));
        OWLOntologyDocumentSource source = new StreamDocumentSource(input);
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        return manager.loadOntologyFromOntologyDocument(source, config);
    }

}
