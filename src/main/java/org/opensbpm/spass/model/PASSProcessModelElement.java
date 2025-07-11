package org.opensbpm.spass.model;

import java.util.Collection;

public interface PASSProcessModelElement {
    /**
     * Gets the id of the process model.
     *
     * @return the id of the process model
     * @see http://www.i2pm.net/standard-pass-ont#hasModelComponentID
     */
    String getId();

    /**
     * Gets the label of the process model.
     *
     * @return the label of the process model
     * @see http://www.i2pm.net/standard-pass-ont#hasModelComponentLabel
     */
    String getLabel();

    /**
     * Gets all property values for the contains property.<p>
     *
     * @return a collection of values for the contains property.
     * @see http://www.i2pm.net/standard-pass-ont#contains
     */
    Collection<? extends PASSProcessModelElement> getContains();

    <T extends PASSProcessModelElement> Collection<T> getContains(Class<T> passClass);

    interface Mutable extends PASSProcessModelElement{
        /**
         * Sets the id of the process model.
         *
         * @param id the id to set
         */
        void setId(String id);

        /**
         * Sets the label of the process model.
         *
         * @param label the label to set
         */
        void setLabel(String label);

        /**
         * Adds a new element to the contains property.
         *
         * @param modelElement the model element to add
         */
        void addContains(PASSProcessModelElement modelElement);

        /**
         * Adds a collection of elements to the contains property.
         *
         * @param modelElements the collection of model elements to add
         */
        void addContains(Collection<? extends PASSProcessModelElement> modelElements);
    }

    interface Builder<T, B extends Builder<T, B>> {
        B withId(String id);

        B withLabel(String label);

        B addContains(Collection<? extends PASSProcessModelElement> modelElements);

        T build();
    }
}
