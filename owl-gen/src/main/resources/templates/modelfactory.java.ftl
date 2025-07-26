package ${packageName};

import ${apiPackageName}.FunctionSpecification;
import ${apiPackageName}.ObjectFactory;
import ${apiPackageName}.PASSProcessModelElement;
import ${apiPackageName}.PASSProcessModelElement.Mutable;
import ${apiPackageName}.State;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.lang.String.format;

public class ModelFactory {

    private static Map<IRI, ModelInstantiator> modelInstantiators = new HashMap<>();
    static {
    <#list classProperties as prop>
        modelInstantiators.put(IRI.create("${prop.iri}"), ObjectFactory::create${prop.name?cap_first});
    </#list >
    }

    private static final Map<IRI, PropertyConsumer> propertyConsumers = new HashMap<>();
    static {
    <#list dataProperties as prop>
        propertyConsumers.put(IRI.create("${prop.iri}"), (PropertyConsumer<${prop.subjectModel.className}.Mutable,${prop.typeName}>)${prop.subjectModel.className}.Mutable::set${prop.name?cap_first});
    </#list >
    }

    private static final Map<IRI, ObjectConsumer> objectConsumers = new HashMap<>();
    static {
    <#list objectProperties as prop>
        <#if prop.multiValue>
            objectConsumers.put(IRI.create("${prop.iri}"), (ObjectConsumer<${prop.subjectModel.className}.Mutable,${prop.objectModel.className}>)${prop.subjectModel.className}.Mutable::add${prop.name?cap_first});
        <#else>
            objectConsumers.put(IRI.create("${prop.iri}"), ${prop.objectModel.className}.Mutable::set${prop.name?cap_first});
        </#if>
    </#list >
    }


    /**
     * Instantiates a mutable PassModelElement based on the given OWLClass.
     *
     * @param iri the IRI representing the PassModelElement
     * @return a Mutable instance of the PassModelElement
     * @throws UnsupportedOperationException if the OWLClass does not have a corresponding PassModelElement
     */
    public static PASSProcessModelElement.Mutable createModelElement(OWLClass owlClass, IRI iri) {
        if (!modelInstantiators.containsKey(owlClass.getIRI())) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have a corresponding PassModelElement", iri));
        }
        return modelInstantiators.get(owlClass.getIRI()).apply(ObjectFactory.getInstance(),iri);
    }

    /**
     * Consumes a property value for a given PassProcessModelElement.
     *
     * @param propertyIRI the IRI of the property to consume
     * @param subject     the PassProcessModelElement to which the property belongs
     * @param object      the OWLLiteral object representing the property value
     * @throws UnsupportedOperationException if the propertyIRI does not have a corresponding ValueConsumer
     */
    public static void consumeProperty(IRI propertyIRI, Mutable subject, OWLLiteral object) {
        if (!propertyConsumers.containsKey(propertyIRI)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have a corresponding ValueConsumer", propertyIRI));
        }
        PropertyConsumer valueConsumer = propertyConsumers.get(propertyIRI);
        Object value;
        if(object.isBoolean()){
            value = Boolean.valueOf(object.parseBoolean());
        }else if(object.isLiteral()){
            value = object.getLiteral();
        }else
            throw new UnsupportedOperationException(String.format("Literal %s is not a supported yet", object));

        valueConsumer.accept(subject, value);
    }

    /**
     * Consumes an object for a given PassProcessModelElement.
     *
     * @param propertyIRI the IRI of the property to consume
     * @param subject     the PassProcessModelElement to which the object belongs
     * @param object      the Mutable object representing the value of the property
     * @throws UnsupportedOperationException if the propertyIRI does not have a corresponding ObjectConsumer
     */
    public static void consumeObject(IRI propertyIRI, Mutable subject, PASSProcessModelElement object) {
        if (!objectConsumers.containsKey(propertyIRI)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding ValueConsumer", propertyIRI));
        }
        ObjectConsumer objectConsumer = objectConsumers.get(propertyIRI);
        objectConsumer.accept(subject, object);
    }

    private interface ModelInstantiator extends BiFunction<ObjectFactory, IRI, Mutable> {

        @Override
        Mutable apply(ObjectFactory objectFactory, IRI iri);
    }

    private interface PropertyConsumer<S extends Mutable, O> extends BiConsumer<S, O> {
    }

    private interface ObjectConsumer<S extends Mutable, O extends PASSProcessModelElement> extends BiConsumer<S, O> {
        @Override
        void accept(S subject, O object);
    }

}
