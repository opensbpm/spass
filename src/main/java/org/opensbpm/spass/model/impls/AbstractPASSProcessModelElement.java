package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.PASSProcessModelElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class AbstractPASSProcessModelElement implements PASSProcessModelElement {

    protected String id;
    protected String label;
    protected List<PASSProcessModelElement> contains;

    protected AbstractPASSProcessModelElement() {
        contains = new ArrayList<>();
    }

    protected AbstractPASSProcessModelElement(AbstractPASSProcessModelElement copy) {
        this.id = copy.id;
        this.label = copy.label;
        this.contains = new ArrayList<>(copy.contains);
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final String getLabel() {
        return label;
    }

    @Override
    public final Collection<? extends PASSProcessModelElement> getContains() {
        return Collections.unmodifiableCollection(contains);
    }

    @Override
    public final <T extends PASSProcessModelElement> Collection<T> getContains(Class<T> passClass) {
        return contains.stream()
                .filter(passClass::isInstance)
                .map(passClass::cast)
                .toList();
    }

    public abstract AbstractPASSProcessModelElement copy();

    protected static abstract class AbstractBuilder<E extends PASSProcessModelElement, T extends AbstractPASSProcessModelElement, B extends AbstractBuilder<E, T, B>> {
        protected final T processModelElement;

        public AbstractBuilder(T processModelElement) {
            this.processModelElement = processModelElement;
        }

        protected abstract B self();

        public final B withId(String id) {
            processModelElement.id = id;
            return self();
        }

        public final B withLabel(String label) {
            processModelElement.label = label;
            return self();
        }

        public final B addContains(Collection<? extends PASSProcessModelElement> modelElements) {
            processModelElement.contains.addAll(modelElements);
            return self();
        }

        public abstract E build();
    }

}
