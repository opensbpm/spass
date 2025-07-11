package org.opensbpm.spass;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class PassOntology {
    private final static String BASE_IRI = "http://www.i2pm.net/standard-pass-ont";

    public static PassOntology loadOwl(InputStream input) throws OWLOntologyCreationException {
        PassOntology passOntology = new PassOntology();
        passOntology.loadOntology(input);
        return passOntology;
    }

    public static IRI createIRI(String shortName) {
        return IRI.create(format("%s#%s", BASE_IRI, shortName));
    }

    private final OWLOntologyManager manager;
    private final PassOwlDataFactory passOwlDataFactory;
    private OWLOntology ontology;

    private PassOntology() {
        manager = OWLManager.createOWLOntologyManager();

        // Add custom IRI mapper to redirect external IRI to local file
        manager.setIRIMappers(asSet(
                createLocalIRIMapper("http://www.i2pm.net/standard-pass-ont", "/standard_PASS_ont_dev.owl")
        ));
        passOwlDataFactory = new PassOwlDataFactory();
    }

    private void loadOntology(InputStream input) throws OWLOntologyCreationException {
        OWLOntologyDocumentSource source = new StreamDocumentSource(input);
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        ontology = manager.loadOntologyFromOntologyDocument(source, config);

    }
    public PassOwlDataFactory getPassOwlDataFactory() {
        return passOwlDataFactory;
    }

    public Stream<OWLNamedIndividual> getIndividuals() {
        return ontology.individualsInSignature();
    }

    public Stream<OWLDataPropertyAssertionAxiom> getDataPropertyAssertions() {
        return ontology.axioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    public OWLClass getTypeOfIndividual(OWLNamedIndividual namedIndividual) {
        return getClassesOfIndividual(namedIndividual)
                .reduce(onlyOne())
                .orElseThrow(() -> new RuntimeException("Now OWLClass found for individual: " + namedIndividual.getIRI()));
    }

    private static BinaryOperator<OWLClass> onlyOne() {
        return (a, b) -> {
            throw new IllegalStateException("Stream contains more than one element");
        };
    }

    public Stream<OWLClass> getClassesOfIndividual(OWLNamedIndividual namedIndividual) {
        return ontology.classAssertionAxioms(namedIndividual)
                .map(OWLClassAssertionAxiom::getClassExpression)
                .map(OWLClassExpression::asOWLClass);
    }

    public Stream<OWLNamedIndividual> retrieveContainsOfClass(OWLNamedIndividual namedIndividual, OWLClass owlClass) {
        return retrieveContains(namedIndividual)
                .filter(individual -> hasClass(individual, owlClass));
    }

    private Stream<OWLNamedIndividual> retrieveContains(OWLNamedIndividual namedIndividual) {
        return retrieveNamedIndividualsByObjectProperty(namedIndividual, passOwlDataFactory.getContainsProperty());
    }

    public Stream<OWLNamedIndividual> retrieveNamedIndividualByClass(OWLClass owlClass) {
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

    public String readDataProperty(OWLNamedIndividual individual, String propertyShortName) {
        for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(individual)) {
            OWLDataProperty property = ax.getProperty().asOWLDataProperty();
            if (property.getIRI().getShortForm().equals(propertyShortName)) {
                return ax.getObject().getLiteral();
            }
        }
        return null; // Not found
    }

    public void dumpIndividuals() {
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

    public void listObjectPropertiesWithDomainAndRange() {
        Set<OWLObjectPropertyAssertionAxiom> axioms = ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);

        for (OWLObjectPropertyAssertionAxiom ax : axioms) {
            OWLIndividual subject = ax.getSubject();
            OWLObjectPropertyExpression property = ax.getProperty();
            OWLIndividual object = ax.getObject();

            System.out.println(subject + " " + property + " " + object);
        }
    }

    public void dumpSuperclasses() {
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


    public final class PassOwlDataFactory {

        public IRI getPassProcessModelIRI() {
            return createIRI("PASSProcessModel");
        }

        public OWLClass getPassProcessModelClass() {
            return getOwlClass(getPassProcessModelIRI());
        }

        /**
         * Generic ObjectProperty that links two model elements where one contains another (possible multiple).
         */
        public OWLObjectProperty getContainsProperty() {
            return getOwlObjectProperty("contains");
        }

        public OWLClass getSubjectBehaviorClass() {
            return getOwlClass("SubjectBehavior");
        }

        public OWLClass getStandardPASSState() {
            return getOwlClass("StandardPASSState");
        }

        public OWLClass getDoState() {
            return getOwlClass("DoState");
        }

        private OWLClass getOwlClass(String shortName) {
            return getOwlClass(createIRI(shortName));
        }

        private OWLClass getOwlClass(IRI iri) {
            return manager.getOWLDataFactory().getOWLClass(iri);
        }

        private OWLObjectProperty getOwlObjectProperty(String shortName) {
            return manager.getOWLDataFactory().getOWLObjectProperty(createIRI(shortName));
        }


    }
}

