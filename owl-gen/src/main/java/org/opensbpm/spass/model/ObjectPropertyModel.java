package org.opensbpm.spass.model;

import org.semanticweb.owlapi.model.IRI;

public class ObjectPropertyModel extends PropertyModel {
    private ClassModel subjectModel;
    private final ClassModel objectModel;
    private ObjectPropertyModel inverseOf;

    public ObjectPropertyModel(ClassModel objectModel, String name, String typeName, boolean multiValue, IRI iri) {
        super(name, typeName, multiValue, iri);
        this.objectModel = objectModel;
    }

    public ClassModel getSubjectModel() {
        return subjectModel;
    }

    public void setSubjectModel(ClassModel subjectModel) {
        this.subjectModel = subjectModel;
    }

    public ClassModel getObjectModel() {
        return objectModel;
    }

    public boolean hasInverseOf() {
        return inverseOf != null;
    }

    public ObjectPropertyModel getInverseOf() {
        return inverseOf;
    }

    public void setInverseOf(ObjectPropertyModel inverseOf) {
        this.inverseOf = inverseOf;
    }
}
