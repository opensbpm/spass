import freemarker.template.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class OWL2JavaGenerator {
    private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

    public static void main(String[] args) throws Exception {
        new OWL2JavaGenerator().run("output", "com.generated");
    }

    public OWL2JavaGenerator() throws IOException {
        cfg.setDefaultEncoding("UTF-8");
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void run(String outputDir, String packageName) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream passStream = OWL2JavaGenerator.class.getResourceAsStream("/standard_PASS_ont_dev.owl");
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(passStream);

        Map<OWLClass, ClassModel> classModels = new HashMap<>();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            String className = getClassName(cls);
            ClassModel classModel = new ClassModel(packageName, className);
            classModels.put(cls, classModel);
        }

        for (OWLDataProperty dataProperty : ontology.getDataPropertiesInSignature()) {
            for (OWLClassExpression domain : EntitySearcher.getDomains(dataProperty, ontology).collect(Collectors.toList())) {
                if (domain.isOWLClass()) {
                    OWLClass cls = domain.asOWLClass();
                    ClassModel classModel = classModels.get(cls);
                    if (classModel != null) {
                        String propertyName = dataProperty.getIRI().getShortForm();

                        String propertyType = EntitySearcher.getRanges(dataProperty, ontology)
                                .findFirst()
                                .map(owlDataRange -> getJavaTypeForOWLDataRange(owlDataRange))
                                .orElse("String");

                        classModel.addProperty(new PropertyModel(propertyName, propertyType));
                    }
                }
            }
        }

        for (OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
            boolean isFunctional = ontology.getAxioms(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)
                    .stream()
                    .anyMatch(ax -> ax.getProperty().equals(objectProperty));

            for (OWLClassExpression domain : EntitySearcher.getDomains(objectProperty, ontology).collect(Collectors.toList())) {
                if (domain.isOWLClass()) {
                    OWLClass cls = domain.asOWLClass();
                    ClassModel classModel = classModels.get(cls);
                    if (classModel != null) {
                        String propertyName = objectProperty.getIRI().getShortForm();

                        String propertyType = EntitySearcher.getRanges(objectProperty, ontology)
                                .findFirst()
                                .filter(owlClassExpression -> owlClassExpression.isOWLClass())
                                .map(owlClassExpression ->classModels.get(owlClassExpression.asOWLClass()))
                                .map(cm -> cm.getClassName())
                                .orElse(null);
                        if(propertyType == null) {
                            System.out.println("Skipping: No range found for property: " + propertyName);
                            continue;
                        }
                        //String propertyType = "Object"; // Default type for object properties

                        System.out.println(propertyName + " is " +
                                (isFunctional ? "single-valued" : "multi-valued (collection)"));

                        classModel.addProperty(new PropertyModel(propertyName, propertyType));
                    }
                }
            }
        }

        for (OWLClass parentClass : ontology.getClassesInSignature()) {
            for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSuperClass(parentClass)) {
                OWLClassExpression sub = axiom.getSubClass();
                if (!sub.isAnonymous()) {
                    OWLClass subClass = sub.asOWLClass();
                    classModels.get(subClass).addExtends(getClassName(parentClass));
                }
            }
        }


        File outputDirFile = Path.of(outputDir).toFile();
        outputDirFile.mkdirs(); // Ensure output directory exists
        System.out.println("Generating Java classes to "+ outputDirFile.getAbsolutePath());
        for (ClassModel classModel : classModels.values()) {
            Template template = cfg.getTemplate("class.ftl");
            File output = new File(outputDir + "/" + classModel.className + ".java");
            try (Writer out = new FileWriter(output)) {
                template.process(classModel, out);
                System.out.println("Generated: " + output.getPath());
            }
        }
    }

    private static String getClassName(OWLClass cls) {
        String className = cls.getIRI().getShortForm();
        className = className.replace('-', '_'); // Replace hyphens with underscores for Java compatibility
        return className;
    }

    private static Set<OWLClass> getAssertedSubClasses(OWLOntology ontology, OWLClass parentClass) {
        Set<OWLClass> subclasses = new HashSet<>();
        for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSuperClass(parentClass)) {
            OWLClassExpression sub = axiom.getSubClass();
            if (!sub.isAnonymous()) {
                subclasses.add(sub.asOWLClass());
            }
        }
        return subclasses;
    }

    private static String getJavaTypeForOWLDataRange(OWLDataRange dataRange) {
        if (dataRange.getDataRangeType() == DataRangeType.DATATYPE) {
            OWLDatatype datatype = dataRange.asOWLDatatype();
            String iri = datatype.getIRI().getShortForm();
            switch (iri) {
                case "string":
                    return "String";
                case "int":
                case "integer":
                    return "Integer";
                case "float":
                    return "Float";
                case "double":
                    return "Double";
                case "boolean":
                    return "Boolean";
                case "dateTime":
                    return "java.time.OffsetDateTime";
                case "date":
                    return "java.time.LocalDate";
                case "time":
                    return "java.time.LocalTime";
                case "dayTimeDuration":
                    return "javax.xml.datatype.Duration";
                default:
                    return "String";
            }
        }
        // For complex or custom ranges, default to String
        return "String";
    }

    public static class ClassModel {
        private String packageName;
        private String className;
        private List<String> extendsClasses = new ArrayList<>();
        private List<PropertyModel> properties = new ArrayList<>();

        public ClassModel(String packageName, String className) {
            this.packageName = packageName;
            this.className = className;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getClassName() {
            return className;
        }

        public List<String> getExtendsClasses() {
            return extendsClasses;
        }

        public void addExtends(String className) {
            extendsClasses.add(className);
        }

        public List<PropertyModel> getProperties() {
            return properties;
        }

        public void addProperty(PropertyModel propertyModel) {
            properties.add(propertyModel);
        }
    }

    public static class PropertyModel {
        private String name;
        private String type;

        public PropertyModel(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
