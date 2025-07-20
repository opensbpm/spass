package org.opensbpm.spass.model;

import java.util.ArrayList;
import java.util.List;

public class ClassModel {

    private String packageName;
    private String apiPackageName;
    private String implPackageName;
    private String className;
    private List<String> extendsTypes = new ArrayList<>();
    private List<String> implementsTypes = new ArrayList<>();
    private List<PropertyModel> properties = new ArrayList<>();

    public ClassModel(String className) {
        this.className = className;
    }

    public ClassModel(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
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

    public List<PropertyModel> getProperties() {
        return properties;
    }

    public void addProperty(PropertyModel propertyModel) {
        properties.add(propertyModel);
    }

    public ClassModel copyToPackage(String packageName) {
        ClassModel model = new ClassModel(packageName, className);
        model.properties = new ArrayList<>(properties);
        return model;
    }

    public ClassModel copyToClassName(String className) {
        ClassModel model = new ClassModel(packageName, className);
        model.properties = new ArrayList<>(properties);
        return model;
    }


}
