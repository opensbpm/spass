package org.opensbpm.spass.model;

import org.semanticweb.owlapi.model.IRI;

public class ObjectPropertyModel extends PropertyModel {
    private final ClassModel subjectModel;
    private final ClassModel objectModel;

    public ObjectPropertyModel(ClassModel subjectModel, ClassModel objectModel,String name, String typeName, boolean multiValue, IRI iri) {
        super(name, typeName, multiValue, iri);
        this.subjectModel = subjectModel;
        this.objectModel = objectModel;
    }

    public ClassModel getSubjectModel() {
        return subjectModel;
    }

    public ClassModel getObjectModel() {
        return objectModel;
    }
}
