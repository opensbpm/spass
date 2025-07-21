package org.opensbpm.spass.model;

import static java.lang.String.format;

public class PropertyModel {
    private String name;
    private String typeName;
    private boolean multiValue;
    private String iri;

    public PropertyModel(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public PropertyModel(String name, String typeName,String iri) {
        this.name = name;
        this.typeName = typeName;
        this.iri = iri;
    }

    public PropertyModel(String name, String typeName, boolean multiValue) {
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
        return isMultiValue() ? format("List<%s>", typeName) : typeName;
    }

    public boolean isMultiValue() {
        return multiValue;
    }

    public String getIri() {
        return iri;
    }

}
