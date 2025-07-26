package org.opensbpm.spass.model;

import org.semanticweb.owlapi.model.IRI;

import static java.lang.String.format;

public class PropertyModel {
    private ClassModel subjectModel;
    private String name;
    private String typeName;
    private boolean multiValue;
    private IRI iri;

    public PropertyModel(ClassModel subjectModel, String name, String typeName, IRI iri) {
        this.subjectModel = subjectModel;
        this.name = name;
        this.typeName = typeName;
        this.iri = iri;
    }

    public PropertyModel(String name, String typeName, boolean multiValue, IRI iri) {
        this.name = name;
        this.typeName = typeName;
        this.multiValue = multiValue;
        this.iri = iri;
    }

    public ClassModel getSubjectModel() {
        return subjectModel;
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

    public IRI getIri() {
        return iri;
    }

}
