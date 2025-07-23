package org.opensbpm.spass.model;

import org.semanticweb.owlapi.model.IRI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ClassModel {
    public static ClassModel of(IRI iri) {
        return new ClassModel(Objects.requireNonNull(iri, "IRI cannot be null"));
    }

    private final IRI iri;
    private final String className;

    private List<ClassModel> superTypes = new ArrayList<>();
    private List<PropertyModel> dataProperties = new ArrayList<>();
    private List<PropertyModel> objectProperties = new ArrayList<>();


    private ClassModel(IRI iri) {
        this.iri = iri;
        String className = iri.getShortForm();
        this.className = className.replace('-', '_'); // Replace hyphens with underscores for Java compatibility
    }

    public IRI getIri() {
        return iri;
    }

    public String getClassName() {
        return className;
    }

    public List<ClassModel> getSuperTypes() {
        return superTypes;
    }

    public void addSuperType(ClassModel classModel) {
        superTypes.add(classModel);
    }

    public List<PropertyModel> getDataProperties() {
        return dataProperties;
    }

    public void addDataProperty(PropertyModel propertyModel) {
        dataProperties.add(propertyModel);
    }

    public List<PropertyModel> getObjectProperties() {
        return objectProperties;
    }

    public void addObjectProperty(PropertyModel propertyModel) {
        objectProperties.add(propertyModel);
    }

    public Stream<PropertyModel> streamProperties() {
        return Stream.concat(dataProperties.stream(), objectProperties.stream());
    }
}
