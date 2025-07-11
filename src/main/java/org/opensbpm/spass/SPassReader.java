package org.opensbpm.spass;

import org.opensbpm.spass.model.DoState;
import org.opensbpm.spass.model.PASSFactory;
import org.opensbpm.spass.model.PASSProcessModel;
import org.opensbpm.spass.model.SubjectBehavior;
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
    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private final PassOwlDataFactory passOwlDataFactory;
    private OWLOntology ontology;

    public SPassReader() {
        // Add custom IRI mapper to redirect external IRI to local file
        manager.setIRIMappers(asSet(
                createLocalIRIMapper("http://www.i2pm.net/standard-pass-ont", "/standard_PASS_ont_dev.owl")
        ));
        passOwlDataFactory = new PassOwlDataFactory();
    }

    public PASSProcessModel read(InputStream input) throws SPassIncompleteException, OWLOntologyCreationException {
        OWLOntologyDocumentSource source = new StreamDocumentSource(input);
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        ontology = manager.loadOntologyFromOntologyDocument(source, config);

        //dumpSuperclasses(ontology);
        dumpIndividuals();

        OWLNamedIndividual passModelIndividual = retrieveNamedIndividualByClass(passOwlDataFactory.getPassProcessModelClass())
                .findFirst()
                .orElseThrow(() -> new SPassIncompleteException("No PASSProcessModel found in ontology"));
        String id = readDataProperty(passModelIndividual, "hasModelComponentID");
        String label = readDataProperty(passModelIndividual, "hasModelComponentLabel");

        List<SubjectBehavior> subjectBehaviors = retrieveContainsOfClass(passModelIndividual, passOwlDataFactory.getSubjectBehaviorClass())
                .map(this::toSubjectBehavior)
                .toList();

        return PASSFactory.getInstance().createPassProcessModel()
                .withId(id)
                .withLabel(label)
                .addContains(subjectBehaviors)
                .build();
    }

    private SubjectBehavior toSubjectBehavior(OWLNamedIndividual subjectBehaviorIndividual) {
        String id = readDataProperty(subjectBehaviorIndividual, "hasModelComponentID");
        String label = readDataProperty(subjectBehaviorIndividual, "hasModelComponentLabel");

        List<DoState> doStates = retrieveContainsOfClass(subjectBehaviorIndividual, passOwlDataFactory.getDoState())
                .map(this::toDoState)
                .toList();

        return PASSFactory.getInstance().createSubjectBehavior()
                .withId(id)
                .withLabel(label)
                .addContains(doStates)
                .build();
    }

    private DoState toDoState(OWLNamedIndividual subjectBehaviorIndividual) {
        String id = readDataProperty(subjectBehaviorIndividual, "hasModelComponentID");
        String label = readDataProperty(subjectBehaviorIndividual, "hasModelComponentLabel");

        return PASSFactory.getInstance().createDoState()
                .withId(id)
                .withLabel(label)
                .build();
    }

    private Stream<OWLNamedIndividual> retrieveContainsOfClass(OWLNamedIndividual namedIndividual, OWLClass owlClass) {
        return retrieveContains(namedIndividual)
                .filter(individual -> hasClass(individual, owlClass));
    }

    private Stream<OWLNamedIndividual> retrieveContains(OWLNamedIndividual namedIndividual) {
        return retrieveNamedIndividualsByObjectProperty(namedIndividual, passOwlDataFactory.getContainsProperty());
    }

    private Stream<OWLNamedIndividual> retrieveNamedIndividualByClass(OWLClass owlClass) {
        return ontology.getIndividualsInSignature().stream()
                .filter(individual -> hasClass(individual, owlClass));
    }

    private boolean hasClass(OWLNamedIndividual individual, OWLClass owlClass) {
        return ontology.getClassAssertionAxioms(individual).stream()
                .map(OWLClassAssertionAxiom::getClassExpression)
                .filter(typeExpr -> !typeExpr.isAnonymous())
                .map(AsOWLClass::asOWLClass)
                .filter(cls -> owlClass.getIRI().equals(cls.getIRI()))
                .count() > 0;
    }

    public Stream<OWLNamedIndividual> retrieveNamedIndividualsByObjectProperty(OWLNamedIndividual individual, OWLObjectProperty owlObjectProperty) {
        return ontology.getObjectPropertyAssertionAxioms(individual).stream()
                .filter(ax -> ax.getProperty().isOWLObjectProperty() &&
                        ax.getObject().isNamed() &&
                        ax.getProperty().asOWLObjectProperty().getIRI().equals(owlObjectProperty.getIRI()))
                .map(ax -> ax.getObject().asOWLNamedIndividual());
    }

    class PassOwlDataFactory {
        private final static String BASE_IRI = "http://www.i2pm.net/standard-pass-ont";

        private OWLClass getPassProcessModelClass() {
            return getOwlClass("PASSProcessModel");
        }

        /**
         * Generic ObjectProperty that links two model elements where one contains another (possible multiple).
         */
        private OWLObjectProperty getContainsProperty() {
            return getOwlObjectProperty("contains");
        }

        private OWLClass getSubjectBehaviorClass() {
            return getOwlClass("SubjectBehavior");
        }

        private OWLClass getStandardPASSState() {
            return getOwlClass("StandardPASSState");
        }

        private OWLClass getDoState() {
            return getOwlClass("DoState");
        }

        private OWLClass getOwlClass(String shortName) {
            return manager.getOWLDataFactory().getOWLClass(IRI.create(format("%s#%s", BASE_IRI, shortName)));
        }

        private OWLObjectProperty getOwlObjectProperty(String shortName) {
            return manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(format("%s#%s", BASE_IRI, shortName)));
        }
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
