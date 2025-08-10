package org.opensbpm.spass;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.opensbpm.spass.java.JavaClass;
import org.opensbpm.spass.java.ModelFactory;
import org.opensbpm.spass.java.JavaProperty;
import org.opensbpm.spass.model.ClassModel;
import org.opensbpm.spass.model.ObjectPropertyModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static java.lang.String.format;
import static java.util.Arrays.asList;

class JavaGenerator {

    private final File outputDirectory;
    private final String packageName;
    private final Configuration cfg;

    public JavaGenerator(File outputDirectory, String packageName) {
        this.outputDirectory = outputDirectory;
        this.packageName = packageName;

        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void generate(Collection<ClassModel> classModels) throws IOException, TemplateException {
        System.out.println("Generating Java classes to " + outputDirectory.getAbsolutePath());
        String apiPackageName = format("%s.api", packageName);
        String implPackageName = format("%s.impl", packageName);

        File implPackageDir = new File(outputDirectory, implPackageName.replace('.', '/'));
        implPackageDir.mkdirs();
        for (ClassModel classModel : classModels) {
            writeFile(JavaClass.of(classModel, apiPackageName), "api.ftl");

            JavaClass implJavaClass = new JavaClass(implPackageName, format("Mutable%s", classModel.getClassName()));
            implJavaClass.setComment(classModel.getComment());

            if (!classModel.getSuperTypes().isEmpty()) {
                String superType = classModel.getSuperTypes().stream()
                        .filter(type -> !"SimplePASSElement".contains(type.getClassName()))
                        .map(type -> type.getClassName())
                        .findFirst()
                        .orElseThrow();
                implJavaClass.addExtendsType("Mutable" + superType);
            }
            implJavaClass.addImplementsType(classModel.getClassName() + ".Mutable");
            implJavaClass.setApiPackageName(apiPackageName);
            classModel.streamProperties()
                    .map(model -> {
                        JavaProperty javaProperty = JavaProperty.of(model);
                        if(model instanceof ObjectPropertyModel objectPropertyModel){
                            if(objectPropertyModel.hasInverseOf())
                                javaProperty.setInverseOf(JavaProperty.of(objectPropertyModel.getInverseOf()));

                        }
                        return javaProperty;
                    })
                    .forEach(implJavaClass::addProperty);
            writeFile(implJavaClass, "class.ftl");
        }
        JavaClass objectFactoryModel = new JavaClass(apiPackageName, "ObjectFactory");
        objectFactoryModel.setImplPackageName(implPackageName);
        objectFactoryModel.setApiPackageName(apiPackageName);
        JavaClass defaultObjectFactoryModel = new JavaClass(implPackageName, "DefaultObjectFactory");
        defaultObjectFactoryModel.addImplementsType("ObjectFactory");
        defaultObjectFactoryModel.setApiPackageName(apiPackageName);

        ModelFactory modelFactory = new ModelFactory(apiPackageName, "ModelFactory");
        modelFactory.setImplPackageName(implPackageName);
        modelFactory.setApiPackageName(apiPackageName);

        JavaClass visitorModel = new JavaClass(packageName, "Visitor");
        visitorModel.setImplPackageName(implPackageName);
        visitorModel.setApiPackageName(apiPackageName);
        for (ClassModel classModel : classModels) {
            if (asList("SimplePASSElement", "AdditionalAttribute", "KeyValuePair").contains(classModel.getIri().getShortForm())) {
                continue;
            }
            String iriString = classModel.getIri().getIRIString();
            objectFactoryModel.addProperty(new JavaProperty(classModel.getClassName(), classModel.getClassName(), iriString));
            defaultObjectFactoryModel.addProperty(new JavaProperty(classModel.getClassName(), classModel.getClassName()));
            modelFactory.addClassProperty(new JavaProperty(classModel.getClassName(), classModel.getClassName(), iriString));
            visitorModel.addProperty(new JavaProperty(classModel.getClassName(), classModel.getClassName(), iriString));
        }
        writeFile(objectFactoryModel, "objectfactory.ftl");
        writeFile(defaultObjectFactoryModel, "defaultobjectfactory.ftl");
        writeFile(visitorModel, "visitor.java.ftl");

        classModels.stream()
                .flatMap(classModel -> classModel.getDataProperties().stream())
                .forEach(modelFactory::addDataProperty);

        classModels.stream()
                .flatMap(classModel -> classModel.getObjectProperties().stream())
                .filter(model -> !asList("hasAdditionalAttribute", "hasKeyValuePair").contains(model.getIri().getShortForm()))
                .forEach(modelFactory::addObjectProperty);

        writeFile(modelFactory, "modelfactory.java.ftl");
        writeFile(new JavaClass(implPackageName, "AbstractElement"), "abstractelement.ftl");

    }

    private void writeFile(JavaClass javaClass, String templateName) throws IOException, TemplateException {
        File packageDir = new File(outputDirectory, javaClass.getPackageName().replace('.', '/'));
        packageDir.mkdirs();

        File outFile = new File(packageDir, javaClass.getClassName() + ".java");
        try (Writer out = new FileWriter(outFile, StandardCharsets.UTF_8)) {
            Template template = cfg.getTemplate(templateName);
            template.process(javaClass, out);
            System.out.println("Generated: " + outFile.getPath());
        }
    }

}
