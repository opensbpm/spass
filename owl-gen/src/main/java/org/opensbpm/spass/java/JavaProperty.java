package org.opensbpm.spass.java;

import org.opensbpm.spass.model.PropertyModel;

import static java.lang.String.format;

public class JavaProperty {
    public static JavaProperty of(PropertyModel model) {
        JavaProperty javaProperty = new JavaProperty(model.getName(), model.getTypeName(), model.getIri().getIRIString());
        javaProperty.multiValue = model.isMultiValue();
        return javaProperty;
    }


    private String name;
    private String typeName;
    private boolean multiValue;
    private String iri;
    private JavaProperty inverseOf;

    public JavaProperty(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public JavaProperty(String name, String typeName, String iri) {
        this.name = name;
        this.typeName = typeName;
        this.iri = iri;
    }

    public JavaProperty(String name, String typeName, boolean multiValue) {
        this.name = name;
        this.typeName = typeName;
        this.multiValue = multiValue;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }
    public String getType() {
        return typeName;
    }

    public boolean isMultiValue() {
        return multiValue;
    }

    public String getIri() {
        return iri;
    }

    public boolean hasInverseOf() {
        return inverseOf != null;
    }

    public JavaProperty getInverseOf() {
        return inverseOf;
    }

    public void setInverseOf(JavaProperty inverseOf) {
        this.inverseOf = inverseOf;
    }
}
