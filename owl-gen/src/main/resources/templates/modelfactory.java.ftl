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
import java.util.function.Function;

import static java.lang.String.format;

public class ModelFactory {
    private static ModelFactory INSTANCE;

    public static synchronized ModelFactory getInstance() {
        if( INSTANCE == null) {
            INSTANCE = new ModelFactory();
        }
        return INSTANCE;
    }

    private static Map<IRI, ModelInstantiator> modelInstantiators = new HashMap<>();
    static {
    <#list classProperties as prop>
        modelInstantiators.put(IRI.create("${prop.iri}"), ObjectFactory::create${prop.name?cap_first});
    </#list >
    }

    private static final Map<IRI, PropertyConsumer> propertyConsumers = new HashMap<>();
    static {
    <#list dataProperties as prop>
        propertyConsumers.put(IRI.create("${prop.iri}"), PASSProcessModelElement.Mutable::setHasModelComponentID);
    </#list >
    }

    private static final Map<IRI, ObjectConsumer> objectConsumers = new HashMap<>();
    static {
    <#list objectProperties as prop>
        objectConsumers.put(IRI.create("${prop.iri}"), PASSProcessModelElement.Mutable::addContains);
    </#list >
    }


    /**
     * Instantiates a mutable PassModelElement based on the given OWLClass.
     *
     * @param iri the IRI representing the PassModelElement
     * @return a Mutable instance of the PassModelElement
     * @throws UnsupportedOperationException if the OWLClass does not have a corresponding PassModelElement
     */
    public PASSProcessModelElement.Mutable getModelElement(IRI iri) {
        if (!modelInstantiators.containsKey(iri)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have a corresponding PassModelElement", iri));
        }
        return modelInstantiators.get(iri).apply(ObjectFactory.getInstance());
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
        valueConsumer.accept(subject, object.getLiteral());
    }

    /**
     * Consumes an object for a given PassProcessModelElement.
     *
     * @param propertyIRI the IRI of the property to consume
     * @param subject     the PassProcessModelElement to which the object belongs
     * @param object      the Mutable object representing the value of the property
     * @throws UnsupportedOperationException if the propertyIRI does not have a corresponding ObjectConsumer
     */
    public static void consumeObject(IRI propertyIRI, Mutable subject, Mutable object) {
        if (!objectConsumers.containsKey(propertyIRI)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding ValueConsumer", propertyIRI));
        }
        ObjectConsumer objectConsumer = objectConsumers.get(propertyIRI);
        objectConsumer.accept(subject, object);
    }

    private interface ModelInstantiator extends Function<ObjectFactory, Mutable> {
    }

    private interface PropertyConsumer extends BiConsumer<Mutable, String> {
    }

    private interface ObjectConsumer extends BiConsumer<Mutable, Mutable> {
        @Override
        void accept(Mutable subject, Mutable object);
    }

}
