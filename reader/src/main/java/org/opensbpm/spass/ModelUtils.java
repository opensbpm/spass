package org.opensbpm.spass;

import org.opensbpm.spass.model.FunctionSpecification;
import org.opensbpm.spass.model.PASSFactory;
import org.opensbpm.spass.model.PASSProcessModelElement;
import org.opensbpm.spass.model.State;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.String.format;

import org.opensbpm.spass.model.PASSProcessModelElement.Mutable;

class ModelUtils {
    private final static String BASE_IRI = "http://www.i2pm.net/standard-pass-ont";

    private static final Map<IRI, ModelInstantiator> classInstantiators = Map.of(
            asIRI("PASSProcessModel"), PASSFactory::createPASSProcessModel,
            asIRI("SubjectBehavior"), PASSFactory::createSubjectBehavior,
            asIRI("DoState"), PASSFactory::createDoState,
            asIRI("DoFunction"), PASSFactory::createDoFunction,
            asIRI("SendState"), PASSFactory::createSendState,
            asIRI("SendFunction"), PASSFactory::createSendFunction,
            asIRI("MessageSpecification"), PASSFactory::createMessageSpecification,
            asIRI("SendTransition"), PASSFactory::createSendTransition
    );

    private static final Map<IRI, PropertyConsumer> propertyConsumers = Map.of(
            asIRI("hasModelComponentID"), PASSProcessModelElement.Mutable::setId,
            asIRI("hasModelComponentLabel"), PASSProcessModelElement.Mutable::setLabel
    );

    private static final Map<IRI, ObjectConsumer> objectConsumers = Map.of(
            asIRI("contains"), PASSProcessModelElement.Mutable::addContains,
            asIRI("hasFunctionSpecification"), new ObjectConsumer() {
                @Override
                public void accept(Mutable mutable, Mutable mutable2) {
                    ((State.Mutable)mutable).setHasFunctionSpecification((FunctionSpecification) mutable2);
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
        return classInstantiators.get(owlClass.getIRI()).apply(PASSFactory.getInstance());
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

    private interface ModelInstantiator extends Function<PASSFactory, Mutable> {
    }

    private interface PropertyConsumer extends BiConsumer<Mutable, String> {
    }

    private interface ObjectConsumer extends BiConsumer<Mutable,Mutable> {
    }

}
