package org.opensbpm.spass;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class OwlUtils {

    static HashSet<OWLOntologyIRIMapper> asSet(OWLOntologyIRIMapper... iriMappers) {
        return new HashSet<>(asList(iriMappers));
    }

    static OWLOntologyIRIMapper createLocalIRIMapper(String externalIRI, String localResource) {
        return createLocalIriMapper(
                IRI.create(externalIRI),
                IRI.create(OwlUtils.class.getResource(localResource))
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

    static void dumpIndividuals(OWLOntology ontology) {
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

    static OWLClass getTypeOfIndividual(OWLOntology ontology, OWLNamedIndividual namedIndividual) {
        return getClassesOfIndividual(ontology, namedIndividual)
                .reduce(onlyOne())
                .orElseThrow(() -> new RuntimeException("No OWLClass found for individual: " + namedIndividual.getIRI()));
    }

    private static BinaryOperator<OWLClass> onlyOne() {
        return (a, b) -> {
            throw new IllegalStateException("Stream contains more than one element");
        };
    }

    private static Stream<OWLClass> getClassesOfIndividual(OWLOntology ontology, OWLNamedIndividual namedIndividual) {
        return ontology.classAssertionAxioms(namedIndividual)
                .map(OWLClassAssertionAxiom::getClassExpression)
                .map(OWLClassExpression::asOWLClass);
    }

}

