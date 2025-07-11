package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.SubjectBehavior;

/**
 * Additional to the subject interaction a PASS Model consist of multiple descriptions of subject&apos;s behaviors. These are graphs described with the means of $BehaviorDescribingComponents$
 * <p>
 * A subject in a model may be linked to more than one behavior.
 *
 * @see http://www.i2pm.net/standard-pass-ont#SubjectBehavior
 */
class DefaultSubjectBehavior extends AbstractPASSProcessModelElement implements SubjectBehavior {
    public static Builder builder() {
        return new Builder();
    }

    private DefaultSubjectBehavior() {
        // no instance creation from outside
    }

    private DefaultSubjectBehavior(DefaultSubjectBehavior copy) {
        super(copy);
    }

    public DefaultSubjectBehavior copy() {
        return new DefaultSubjectBehavior(this);
    }

    public static final class Builder extends AbstractBuilder<SubjectBehavior, DefaultSubjectBehavior, Builder> implements SubjectBehavior.Builder {

        private Builder() {
            super(new DefaultSubjectBehavior());
        }

        @Override
        protected Builder self() {
            return this;
        }

        public SubjectBehavior build() {
            return processModelElement.copy();
        }
    }
}
