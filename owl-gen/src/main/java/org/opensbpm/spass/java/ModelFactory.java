package org.opensbpm.spass.java;

import java.util.ArrayList;
import java.util.Collection;

public class ModelFactory extends JavaClass {
    private Collection<JavaProperty> classProperties = new ArrayList<>();
    private Collection<JavaProperty> dataProperties = new ArrayList<>();
    private Collection<JavaProperty> objectProperties = new ArrayList<>();

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

    public Collection<JavaProperty> getDataProperties() {
        return dataProperties;
    }

    public void addDataProperty(JavaProperty propertyConsumer) {
        this.dataProperties.add(propertyConsumer);
    }

    public Collection<JavaProperty> getObjectProperties() {
        return objectProperties;
    }

    public void addObjectProperty(JavaProperty objectConsumer) {
        this.objectProperties.add(objectConsumer);
    }
}
