package org.opensbpm.spass;

import org.opensbpm.spass.model.PASSFactory;
import org.opensbpm.spass.model.PASSProcessModelElement;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.opensbpm.spass.PassOntology.createIRI;

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

    public static PASSProcessModelElement.Mutable instantiate(OWLClass owlClass) {
        if (!iriModelInstantiatorMap.containsKey(owlClass.getIRI())) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding PassModelElement", owlClass.getIRI()));
        }
        return iriModelInstantiatorMap.get(owlClass.getIRI()).apply(PASSFactory.getInstance());
    }

    public static void invoke(IRI propertyIRI, PASSProcessModelElement.Mutable passProcessModelElement, OWLLiteral object) {
        if (!iriValueConsumer.containsKey(propertyIRI)) {
            throw new UnsupportedOperationException(String.format("IRI %s doesn't have an corresponding ValueConsumer", propertyIRI));
        }
        ValueConsumer valueConsumer = iriValueConsumer.get(propertyIRI);
        valueConsumer.accept(passProcessModelElement, object.getLiteral());
    }

    interface ModelInstantiator extends Function<PASSFactory, PASSProcessModelElement.Mutable> {
    }

    interface ValueConsumer extends BiConsumer<PASSProcessModelElement.Mutable, String> {
    }

}
