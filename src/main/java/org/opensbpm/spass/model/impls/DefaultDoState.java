package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.DoState;

/**
 * Additional to the subject interaction a PASS Model consist of multiple descriptions of subject&apos;s behaviors. These are graphs described with the means of $BehaviorDescribingComponents$
 * <p>
 * A subject in a model may be linked to more than one behavior.
 *
 * @see http://www.i2pm.net/standard-pass-ont#SubjectBehavior
 */
class DefaultDoState extends AbstractPASSProcessModelElement implements DoState {
    public static Builder builder() {
        return new Builder();
    }

    private DefaultDoState() {
        // no instance creation from outside
    }

    private DefaultDoState(DefaultDoState copy) {
        super(copy);
    }

    public DefaultDoState copy() {
        return new DefaultDoState(this);
    }

    public static final class Builder extends AbstractBuilder<DoState, DefaultDoState, Builder> implements DoState.Builder {

        private Builder() {
            super(new DefaultDoState());
        }

        @Override
        protected Builder self() {
            return this;
        }

        public DoState build() {
            return processModelElement.copy();
        }
    }
}
