package org.opensbpm.spass.model;

import org.opensbpm.spass.SPassReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The main class that contains all relevant process elements
 *
 * @see http://www.i2pm.net/standard-pass-ont#PASSProcessModel
 */
public class PASSProcessModel extends AbstractPASSProcessModelElement {
    public static Builder builder() {
        return new Builder();
    }

    private PASSProcessModel() {
    }

    private PASSProcessModel(PASSProcessModel copy) {
        super(copy);
    }

    public PASSProcessModel copy() {
        return new PASSProcessModel(this);
    }

    public static final class Builder extends AbstractBuilder<PASSProcessModel, Builder> {

        private Builder() {
            super(new PASSProcessModel());
        }

        @Override
        protected Builder self() {
            return this;
        }

        public PASSProcessModel build() {
            return processModelElement.copy();
        }
    }

}
