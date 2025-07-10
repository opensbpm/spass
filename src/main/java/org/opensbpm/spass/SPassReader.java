package org.opensbpm.spass;

import org.opensbpm.spass.model.ProcessModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class SPassReader {
    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private OWLOntology ontology;

    public SPassReader() {
        // Add custom IRI mapper to redirect external IRI to local file
        manager.setIRIMappers(asSet(
                createLocalIRIMapper("http://www.i2pm.net/standard-pass-ont", "/standard_PASS_ont_dev.owl")
        ));
    }

    public ProcessModel read(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {
        OWLOntologyDocumentSource source = new StreamDocumentSource(input);
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        ontology = manager.loadOntologyFromOntologyDocument(source, config);

        //dumpSuperclasses(ontology);
        dumpIndividuals();

        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        OWLClass passProcessModelClass = dataFactory.getOWLClass(IRI.create("http://www.i2pm.net/standard-pass-ont#PASSProcessModel"));

        OWLNamedIndividual passModelIndividual = ontology.getIndividualsInSignature().stream()
                .filter(individual -> ontology.getClassAssertionAxioms(individual).stream()
                        .map(axiom -> axiom.getClassExpression())
                        .filter(typeExpr -> !typeExpr.isAnonymous())
                        .map(typeExpr -> typeExpr.asOWLClass())
                        .filter(cls -> passProcessModelClass.getIRI().equals(cls.getIRI()))
                        .count() > 0
                )
                .findFirst()
                .orElseThrow(() -> new SPassIncompleteException("No PASSProcessModel found in ontology"));
        String id = readDataProperty(passModelIndividual, "hasModelComponentID");
        String label = readDataProperty(passModelIndividual, "hasModelComponentLabel");

        ProcessModel processModel = new ProcessModel() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getLabel() {
                return label;
            }
        };


        return processModel;
    }

    private void dumpIndividuals() {
        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
            String className = individual.getIRI().getShortForm();
            System.out.println("🦉 Class : " + className);

            Set<OWLClassAssertionAxiom> assertions = ontology.getClassAssertionAxioms(individual);

            for (OWLClassAssertionAxiom ax : assertions) {
                OWLClassExpression typeExpr = ax.getClassExpression();

                if (!typeExpr.isAnonymous()) {
                    OWLClass type = typeExpr.asOWLClass();
                    System.out.println("🧩 Type: " + type.getIRI().getShortForm());

                    Set<OWLSubClassOfAxiom> subClasses = ontology.getSubClassAxiomsForSubClass(type);
                    for (OWLSubClassOfAxiom subClass : subClasses) {
                        System.out.println("🔼 Superclass: " + subClass);
                    }
                }
            }

            for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(individual)) {
                OWLDataProperty property = ax.getProperty().asOWLDataProperty();
                OWLLiteral value = ax.getObject();

                System.out.println("📌 Data Property: " + property.getIRI().getShortForm()
                        + " = " + value.getLiteral());
            }

            for (OWLObjectPropertyAssertionAxiom ax : ontology.getObjectPropertyAssertionAxioms(individual)) {
                OWLObjectPropertyExpression prop = ax.getProperty();
                OWLIndividual object = ax.getObject();

                if (!prop.isAnonymous() && object.isNamed()) {
                    OWLObjectProperty namedProp = prop.asOWLObjectProperty();
                    OWLNamedIndividual target = object.asOWLNamedIndividual();

                    System.out.println("🔗 Object Property: " + namedProp.getIRI().getShortForm()
                            + " → " + target.getIRI().getShortForm());
                }
            }
        }
    }

    private String readDataProperty(OWLNamedIndividual individual, String propertyShortName) {
        for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(individual)) {
            OWLDataProperty property = ax.getProperty().asOWLDataProperty();
            if (property.getIRI().getShortForm().equals(propertyShortName)) {
                return ax.getObject().getLiteral();
            }
        }
        return null; // Not found
    }

    private void dumpSuperclasses() {
        for (OWLClass cls : ontology.getClassesInSignature()) {
            Set<OWLClass> directSupers = ontology.getSubClassAxiomsForSubClass(cls).stream()
                    .map(OWLSubClassOfAxiom::getSuperClass)
                    .filter(ce -> !ce.isAnonymous())
                    .map(ce -> ce.asOWLClass())
                    .collect(Collectors.toSet());

            Set<OWLClass> directSubs = ontology.getSubClassAxiomsForSuperClass(cls).stream()
                    .map(OWLSubClassOfAxiom::getSubClass)
                    .filter(ce -> !ce.isAnonymous())
                    .map(ce -> ce.asOWLClass())
                    .collect(Collectors.toSet());

            System.out.println("Class: " + cls);
            System.out.println("  Asserted Superclasses: " + directSupers);
            System.out.println("  Asserted Subclasses: " + directSubs);
        }
    }

    private static HashSet<OWLOntologyIRIMapper> asSet(OWLOntologyIRIMapper... iriMappers) {
        return new HashSet<>(asList(iriMappers));
    }

    private static OWLOntologyIRIMapper createLocalIRIMapper(String externalIRI, String localResource) {
        return createLocalIriMapper(
                IRI.create(externalIRI),
                IRI.create(SPassReader.class.getResource(localResource))
        );
    }

    private static OWLOntologyIRIMapper createLocalIriMapper(IRI externalIRI, IRI localFileIRI) {
        return new OWLOntologyIRIMapper() {

            @Nullable
            @Override
            public IRI getDocumentIRI(IRI ontologyIRI) {
                if (externalIRI.equals(ontologyIRI)) {
                    return localFileIRI;
                }
                return null;
            }
        };
    }


}
