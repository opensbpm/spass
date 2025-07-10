package org.opensbpm.spass.model;

/**
 * Additional to the subject interaction a PASS Model consist of multiple descriptions of subject&apos;s behaviors. These are graphs described with the means of $BehaviorDescribingComponents$
 * <p>
 * A subject in a model may be linked to more than one behavior.
 *
 * @see http://www.i2pm.net/standard-pass-ont#SubjectBehavior
 */
public class SubjectBehavior extends AbstractPASSProcessModelElement {
    public static Builder builder() {
        return new Builder();
    }

    private SubjectBehavior() {
    }

    private SubjectBehavior(SubjectBehavior copy) {
        super(copy);
    }

    public SubjectBehavior copy() {
        return new SubjectBehavior(this);
    }

    public static final class Builder extends AbstractBuilder<SubjectBehavior, Builder> {

        private Builder() {
            super(new SubjectBehavior());
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
