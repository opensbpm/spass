package org.opensbpm.spass.java;

import org.opensbpm.spass.model.ClassModel;
import org.opensbpm.spass.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaClass {

    public static JavaClass of(ClassModel classModel, String packageName) {
        JavaClass javaClass = new JavaClass(packageName, classModel.getClassName());
        javaClass.setComment(classModel.getComment());
        javaClass.properties = classModel.streamProperties()
                .map(JavaProperty::of)
                .collect(Collectors.toList());

        javaClass.extendsTypes = classModel.getSuperTypes().stream()
                .map(ClassModel::getClassName)
                .collect(Collectors.toList());

        return javaClass;
    }

    private String packageName;
    private String comment;
    private String apiPackageName;
    private String implPackageName;
    private String name;
    private String className;
    private List<String> extendsTypes = new ArrayList<>();
    private List<String> implementsTypes = new ArrayList<>();
    private List<JavaProperty> properties = new ArrayList<>();

    public JavaClass(String className) {
        this.className = className;
    }

    public JavaClass(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }
    public JavaClass(String packageName, String name, String className) {
        this.packageName = packageName;
        this.name = name;
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getApiPackageName() {
        return apiPackageName;
    }

    public void setApiPackageName(String apiPackageName) {
        this.apiPackageName = apiPackageName;
    }

    public String getImplPackageName() {
        return implPackageName;
    }

    public void setImplPackageName(String implPackageName) {
        this.implPackageName = implPackageName;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getExtendsTypes() {
        return extendsTypes;
    }

    public void addExtendsType(String className) {
        extendsTypes.add(className);
    }

    public List<String> getImplementsTypes() {
        return implementsTypes;
    }

    public void addImplementsType(String className) {
        implementsTypes.add(className);
    }

    public List<JavaProperty> getProperties() {
        return properties;
    }

    public void addProperty(JavaProperty javaProperty) {
        properties.add(javaProperty);
    }


}
