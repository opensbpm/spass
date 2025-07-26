package org.opensbpm.spass.java;

import org.opensbpm.spass.model.ObjectPropertyModel;
import org.opensbpm.spass.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collection;

public class ModelFactory extends JavaClass {
    private Collection<JavaProperty> classProperties = new ArrayList<>();
    private Collection<PropertyModel> dataProperties = new ArrayList<>();
    private Collection<ObjectPropertyModel> objectProperties = new ArrayList<>();

    public ModelFactory(String className) {
        super(className);
    }

    public ModelFactory(String packageName, String className) {
        super(packageName, className);
    }

    public Collection<JavaProperty> getClassProperties() {
        return classProperties;
    }

    public void addClassProperty(JavaProperty modelInstantiator) {
        this.classProperties.add(modelInstantiator);
    }

    public Collection<PropertyModel> getDataProperties() {
        return dataProperties;
    }

    public void addDataProperty(PropertyModel dataProperty) {
        this.dataProperties.add(dataProperty);
    }

    public Collection<ObjectPropertyModel> getObjectProperties() {
        return objectProperties;
    }

    public void addObjectProperty(ObjectPropertyModel objectProperty) {
        this.objectProperties.add(objectProperty);
    }
}
