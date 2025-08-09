package org.opensbpm.spass;

import org.opensbpm.spass.model.ClassModel;
import org.opensbpm.spass.model.ObjectPropertyModel;
import org.opensbpm.spass.model.PropertyModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableCollection;

public class OwlReader {

    public Collection<ClassModel> parse(File inputFile) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(inputFile);

        Map<OWLClass, ClassModel> classModels = new HashMap<>();
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            String comment = EntitySearcher.getAnnotations(owlClass, ontology,
                            ontology.getOWLOntologyManager().getOWLDataFactory().getRDFSComment())
                    .filter(a -> a.getValue().isLiteral())
                    .map(a -> a.getValue().asLiteral().get().getLiteral())
                    .findFirst()
                    .orElse(null);

            classModels.put(owlClass, ClassModel.of(owlClass.getIRI(), comment));
        }

        for (OWLDataProperty dataProperty : ontology.getDataPropertiesInSignature()) {
            List<OWLClassExpression> domainExpressions = EntitySearcher.getDomains(dataProperty, ontology).toList();
            for (OWLClassExpression domain : domainExpressions) {
                if (domain.isOWLClass()) {
                    OWLClass cls = domain.asOWLClass();
                    ClassModel classModel = classModels.get(cls);
                    if (classModel != null) {
                        String propertyName = dataProperty.getIRI().getShortForm();

                        String propertyType = EntitySearcher.getRanges(dataProperty, ontology)
                                .findFirst()
                                .map(owlDataRange -> getJavaTypeForOWLDataRange(owlDataRange))
                                .orElse("String");

                        classModel.addDataProperty(new PropertyModel(classModel, propertyName, propertyType, dataProperty.getIRI()));
                    }
                }
            }
        }

        Map<OWLObjectProperty, ObjectPropertyModel> objectPropertyModels = new HashMap<>();

        for (OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
            boolean isFunctional = ontology.getAxioms(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)
                    .stream()
                    .anyMatch(ax -> ax.getProperty().equals(objectProperty));
            boolean hasInversOf = ontology.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES)
                    .stream()
                    .anyMatch(ax -> ax.getFirstProperty().equals(objectProperty));

            String propertyName = objectProperty.getIRI().getShortForm();

            EntitySearcher.getRanges(objectProperty, ontology)
                    .findFirst()
                    .filter(owlClassExpression -> owlClassExpression.isOWLClass())
                    .map(owlClassExpression -> classModels.get(owlClassExpression.asOWLClass()))
                    .ifPresent(objectClassModel -> {
                        String propertyType = objectClassModel.getClassName();
//                        if (propertyType == null) {
//                            System.out.println("Skipping: No range found for property: " + propertyName);
//                            continue;
//                        }
                        //String propertyType = "Object"; // Default type for object properties

                        Set<OWLObjectPropertyExpression> inversProperties = EntitySearcher.getInverses(objectProperty, ontology)
                                .collect(Collectors.toSet());

                        //System.out.println(propertyName + " is " + (isFunctional ? "single-valued" : "multi-valued (collection)"));
                        System.out.println(propertyName + (hasInversOf ? " is invers of " + inversProperties.stream()
                                .map(prp -> prp.toString())
                                .collect(Collectors.joining()) : " no invers"));

                        ObjectPropertyModel objectPropertyModel = new ObjectPropertyModel(objectClassModel, propertyName, propertyType, !isFunctional, objectProperty.getIRI());
                        objectPropertyModels.put(objectProperty, objectPropertyModel);
                    });
        }


        // Handle inverse properties
        for (OWLInverseObjectPropertiesAxiom inverseProperty : ontology.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
            if (objectPropertyModels.containsKey(inverseProperty.getFirstProperty()) &&
                    objectPropertyModels.containsKey(inverseProperty.getSecondProperty())) {
                // Set the inverse relationship
                objectPropertyModels.get(inverseProperty.getFirstProperty()).setInverseOf(objectPropertyModels.get(inverseProperty.getSecondProperty()));
            } else {
                System.out.println("Skipping inverse property: " + inverseProperty.getFirstProperty() + " and " + inverseProperty.getSecondProperty() +
                        " because one of them is not defined in the ontology.");
            }
        }

        for (OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
            if (!objectPropertyModels.containsKey(objectProperty)) {
                System.out.println("Skipping objectProperty " + objectProperty);
                continue; // Skip if domain is not a class
            }
            for (OWLClassExpression domain : EntitySearcher.getDomains(objectProperty, ontology).collect(Collectors.toList())) {
                if (!domain.isOWLClass()) {
                    System.out.println("Skipping domain: " + domain + " for object property: " + objectProperty.getIRI().getShortForm());
                    continue; // Skip if domain is not a class
                }
                OWLClass cls = domain.asOWLClass();
                ClassModel classModel = classModels.get(cls);

                classModel.addObjectProperty(objectPropertyModels.get(objectProperty));
            }
        }


        for (OWLClass parentClass : ontology.getClassesInSignature()) {
            for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(parentClass)) {
                OWLClassExpression superClassExpr = axiom.getSuperClass();
                if (!superClassExpr.isAnonymous()) {
                    OWLClass superClass = superClassExpr.asOWLClass();
                    classModels.get(parentClass).addSuperType(classModels.get(superClass));
                }
            }
        }

        return unmodifiableCollection(classModels.values());
    }

    private static Set<OWLClass> getAssertedSubClasses(OWLOntology ontology, OWLClass parentClass) {
        Set<OWLClass> subclasses = new HashSet<>();
        for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSuperClass(parentClass)) {
            OWLClassExpression sub = axiom.getSubClass();
            if (!sub.isAnonymous()) {
                subclasses.add(sub.asOWLClass());
            }
        }
        return subclasses;
    }

    private static String getJavaTypeForOWLDataRange(OWLDataRange dataRange) {
        if (dataRange.getDataRangeType() == DataRangeType.DATATYPE) {
            OWLDatatype datatype = dataRange.asOWLDatatype();
            String iri = datatype.getIRI().getShortForm();
            switch (iri) {
                case "string":
                    return "String";
                case "int":
                case "integer":
                    return "Integer";
                case "float":
                    return "Float";
                case "double":
                    return "Double";
                case "boolean":
                    return "Boolean";
                case "dateTime":
                    return "java.time.OffsetDateTime";
                case "date":
                    return "java.time.LocalDate";
                case "time":
                    return "java.time.LocalTime";
                case "dayTimeDuration":
                    return "javax.xml.datatype.Duration";
                default:
                    return "String";
            }
        }
        // For complex or custom ranges, default to String
        return "String";
    }

}
