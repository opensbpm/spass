package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.PASSProcessModel;

/**
 * The main class that contains all relevant process elements
 *
 * @see http://www.i2pm.net/standard-pass-ont#PASSProcessModel
 */
class DefaultPASSProcessModel extends AbstractPASSProcessModelElement implements PASSProcessModel {
    public static Builder builder() {
        return new Builder();
    }

    private DefaultPASSProcessModel() {
        // no instance creation from outside
    }

    private DefaultPASSProcessModel(DefaultPASSProcessModel copy) {
        super(copy);
    }

    public DefaultPASSProcessModel copy() {
        return new DefaultPASSProcessModel(this);
    }

    public static final class Builder extends AbstractBuilder<PASSProcessModel, DefaultPASSProcessModel, Builder> implements PASSProcessModel.Builder {

        private Builder() {
            super(new DefaultPASSProcessModel());
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
