package org.opensbpm.spass;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.opensbpm.spass.model.ClassModel;
import org.opensbpm.spass.model.PropertyModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import static java.lang.String.format;

class JavaGenerator {

    private final File outputDirectory;
    private final String packageName;
    private final Configuration cfg;

    public JavaGenerator(File outputDirectory, String packageName) {
        this.outputDirectory = outputDirectory;
        this.packageName = packageName;

        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding("UTF-8");
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
            writeFile(classModel.copyToPackage(apiPackageName), "api.ftl");

            ClassModel implClassModel = new ClassModel(implPackageName, format("Mutable%s", classModel.getClassName()));
            implClassModel.addImplementsType(classModel.getClassName() + ".Mutable");
            implClassModel.setApiPackageName(apiPackageName);
            for (PropertyModel property : classModel.getProperties()) {
                implClassModel.addProperty(property);
            }
            writeFile(implClassModel, "class.ftl");
        }
        ClassModel objectFactoryModel = new ClassModel(apiPackageName, "ObjectFactory");
        objectFactoryModel.setImplPackageName(implPackageName);
        ClassModel defaultObjectFactoryModel = new ClassModel(implPackageName, "DefaultObjectFactory");
        defaultObjectFactoryModel.addImplementsType("ObjectFactory");
        defaultObjectFactoryModel.setApiPackageName(apiPackageName);
        for (ClassModel classModel : classModels) {
            objectFactoryModel.addProperty(new PropertyModel(classModel.getClassName(), classModel.getClassName()));
            defaultObjectFactoryModel.addProperty(new PropertyModel(classModel.getClassName(), classModel.getClassName()));
        }
        writeFile(objectFactoryModel, "objectfactory.ftl");
        writeFile(defaultObjectFactoryModel, "defaultobjectfactory.ftl");
    }

    private void writeFile(ClassModel classModel, String templateName) throws IOException, TemplateException {
        File packageDir = new File(outputDirectory, classModel.getPackageName().replace('.', '/'));
        packageDir.mkdirs();

        File outFile = new File(packageDir, classModel.getClassName() + ".java");
        try (Writer out = new FileWriter(outFile)) {
            Template template = cfg.getTemplate(templateName);
            template.process(classModel, out);
            System.out.println("Generated: " + outFile.getPath());
        }
    }

}
