package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.PASSProcessModelElement;

import java.util.*;

class MutablePASSProcessModelElement implements PASSProcessModelElement, PASSProcessModelElement.Mutable {

    private String id;
    private String label;
    private final List<PASSProcessModelElement> contains = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
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

    @Override
    public void addContains(PASSProcessModelElement modelElement) {
        contains.add(modelElement);
    }

    @Override
    public void addContains(Collection<? extends PASSProcessModelElement> modelElements) {
        contains.addAll(modelElements);
    }

}
