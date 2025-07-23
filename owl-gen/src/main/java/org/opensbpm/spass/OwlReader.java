package org.opensbpm.spass;

import org.opensbpm.spass.model.ClassModel;
import org.opensbpm.spass.model.PropertyModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableCollection;

public class OwlReader {

    public Collection<ClassModel> parse(File inputFile) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(inputFile);

        Map<OWLClass, ClassModel> classModels = new HashMap<>();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            classModels.put(cls, ClassModel.of(cls.getIRI()));
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

                        classModel.addDataProperty(new PropertyModel(propertyName, propertyType, dataProperty.getIRI()));
                    }
                }
            }
        }

        for (OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
            boolean isFunctional = ontology.getAxioms(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)
                    .stream()
                    .anyMatch(ax -> ax.getProperty().equals(objectProperty));

            for (OWLClassExpression domain : EntitySearcher.getDomains(objectProperty, ontology).collect(Collectors.toList())) {
                if (domain.isOWLClass()) {
                    OWLClass cls = domain.asOWLClass();
                    ClassModel classModel = classModels.get(cls);
                    if (classModel != null) {
                        String propertyName = objectProperty.getIRI().getShortForm();

                        String propertyType = EntitySearcher.getRanges(objectProperty, ontology)
                                .findFirst()
                                .filter(owlClassExpression -> owlClassExpression.isOWLClass())
                                .map(owlClassExpression -> classModels.get(owlClassExpression.asOWLClass()))
                                .map(cm -> cm.getClassName())
                                .orElse(null);
                        if (propertyType == null) {
                            System.out.println("Skipping: No range found for property: " + propertyName);
                            continue;
                        }
                        //String propertyType = "Object"; // Default type for object properties

                        System.out.println(propertyName + " is " +
                                (isFunctional ? "single-valued" : "multi-valued (collection)"));

                        classModel.addObjectProperty(new PropertyModel(propertyName, propertyType, !isFunctional, objectProperty.getIRI()));
                    }
                }
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
