package org.opensbpm.spass.model;

/**
 * Additional to the subject interaction a PASS Model consist of multiple descriptions of subject&apos;s behaviors. These are graphs described with the means of $BehaviorDescribingComponents$
 * <p>
 * A subject in a model may be linked to more than one behavior.
 *
 * @see http://www.i2pm.net/standard-pass-ont#SubjectBehavior
 */
public interface SubjectBehavior extends PASSProcessModelElement {

    interface Builder extends PASSProcessModelElement.Builder<SubjectBehavior, Builder> {
    }
}
