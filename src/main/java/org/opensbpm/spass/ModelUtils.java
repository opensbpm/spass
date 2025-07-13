package org.opensbpm.spass;

import org.opensbpm.spass.model.PASSFactory;
import org.opensbpm.spass.model.PASSProcessModelElement;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.String.format;

import org.opensbpm.spass.model.PASSProcessModelElement.Mutable;

class ModelUtils {
    private static final Map<IRI, ModelInstantiator> iriModelInstantiatorMap = new HashMap<>();

    static {
        iriModelInstantiatorMap.put(createIRI("PASSProcessModel"), PASSFactory::createPASSProcessModel);
        iriModelInstantiatorMap.put(createIRI("SubjectBehavior"), PASSFactory::createSubjectBehavior);
        iriModelInstantiatorMap.put(createIRI("DoState"), PASSFactory::createDoState);
    }

    private static final Map<IRI, ValueConsumer> iriValueConsumer = new HashMap<>();

    static {
        iriValueConsumer.put(createIRI("hasModelComponentID"), PASSProcessModelElement.Mutable::setId);
        iriValueConsumer.put(createIRI("hasModelComponentLabel"), PASSProcessModelElement.Mutable::setLabel);
    }

    private static final Map<IRI, ObjectConsumer> iriObjectConsumer = new HashMap<>();

    static {
        iriObjectConsumer.put(createIRI("contains"), PASSProcessModelElement.Mutable::addContains);
    }

    private final static String BASE_IRI = "http://www.i2pm.net/standard-pass-ont";

    public static IRI createIRI(String shortName) {
        return IRI.create(format("%s#%s", BASE_IRI, shortName));
    }

    public static Mutable instantiate(OWLClass owlClass) {
        if (!iriModelInstantiatorMap.containsKey(owlClass.getIRI())) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding PassModelElement", owlClass.getIRI()));
        }
        return iriModelInstantiatorMap.get(owlClass.getIRI()).apply(PASSFactory.getInstance());
    }

    public static void invoke(IRI propertyIRI, Mutable passProcessModelElement, OWLLiteral object) {
        if (!iriValueConsumer.containsKey(propertyIRI)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding ValueConsumer", propertyIRI));
        }
        ValueConsumer valueConsumer = iriValueConsumer.get(propertyIRI);
        valueConsumer.accept(passProcessModelElement, object.getLiteral());
    }

    public static void invoke2(IRI propertyIRI, Mutable subject, Mutable object) {
        if (!iriObjectConsumer.containsKey(propertyIRI)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding ValueConsumer", propertyIRI));
        }
        ObjectConsumer objectConsumer = iriObjectConsumer.get(propertyIRI);
        objectConsumer.accept(subject, object);
    }

    interface ModelInstantiator extends Function<PASSFactory, Mutable> {
    }

    interface ValueConsumer extends BiConsumer<Mutable, String> {
    }

    interface ObjectConsumer extends BiConsumer<Mutable, Mutable> {
    }

}
