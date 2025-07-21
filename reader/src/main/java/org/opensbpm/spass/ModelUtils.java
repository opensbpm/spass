package org.opensbpm.spass;

import org.opensbpm.spass.reader.model.api.FunctionSpecification;
import org.opensbpm.spass.reader.model.api.ObjectFactory;
import org.opensbpm.spass.reader.model.api.PASSProcessModelElement;
import org.opensbpm.spass.reader.model.api.PASSProcessModelElement.Mutable;
import org.opensbpm.spass.reader.model.api.State;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.String.format;

class ModelUtils {
    private final static String BASE_IRI = "http://www.i2pm.net/standard-pass-ont";

    private static final Map<IRI, ModelInstantiator> classInstantiators = Map.of(
            asIRI("PASSProcessModel"), ObjectFactory::createPASSProcessModel,
            asIRI("SubjectBehavior"), ObjectFactory::createSubjectBehavior,
            asIRI("DoState"), ObjectFactory::createDoState,
            asIRI("DoFunction"), ObjectFactory::createDoFunction,
            asIRI("SendState"), ObjectFactory::createSendState,
            asIRI("SendFunction"), ObjectFactory::createSendFunction,
            asIRI("MessageSpecification"), ObjectFactory::createMessageSpecification,
            asIRI("SendTransition"), ObjectFactory::createSendTransition
    );

    private static final Map<IRI, PropertyConsumer> propertyConsumers = Map.of(
            asIRI("hasModelComponentID"), PASSProcessModelElement.Mutable::setHasModelComponentID,
            asIRI("hasModelComponentLabel"), PASSProcessModelElement.Mutable::setHasModelComponentLabel
    );

    private static final Map<IRI, ObjectConsumer> objectConsumers = Map.of(
            //asIRI("contains"), PASSProcessModelElement.Mutable::setContains,
            asIRI("hasFunctionSpecification"), new ObjectConsumer() {
                @Override
                public void accept(Mutable mutable, Mutable mutable2) {
                    //((State.Mutable)mutable).setHasFunctionSpecification((FunctionSpecification) mutable2);
                }
            }
    );

    private static IRI asIRI(String shortName) {
        return IRI.create(format("%s#%s", BASE_IRI, shortName));
    }

    /**
     * Instantiates a mutable PassModelElement based on the given OWLClass.
     *
     * @param owlClass the OWLClass representing the PassModelElement
     * @return a Mutable instance of the PassModelElement
     * @throws UnsupportedOperationException if the OWLClass does not have a corresponding PassModelElement
     */
    public static Mutable instantiateClass(OWLClass owlClass) {
        if (!classInstantiators.containsKey(owlClass.getIRI())) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding PassModelElement", owlClass.getIRI()));
        }
        return classInstantiators.get(owlClass.getIRI()).apply(ObjectFactory.getInstance());
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
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding ValueConsumer", propertyIRI));
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

    private interface ObjectConsumer extends BiConsumer<Mutable,Mutable> {
    }

    public static <T extends PASSProcessModelElement> Collection<T> getContains(PASSProcessModelElement modelElement, Class<T> passClass) {
        return modelElement.getContains().stream()
                .filter(passClass::isInstance)
                .map(passClass::cast)
                .toList();
    }

}
