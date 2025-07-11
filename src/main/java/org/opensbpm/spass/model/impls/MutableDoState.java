package org.opensbpm.spass.model.impls;

import org.opensbpm.spass.model.DoState;

/**
 * Additional to the subject interaction a PASS Model consist of multiple descriptions of subject&apos;s behaviors. These are graphs described with the means of $BehaviorDescribingComponents$
 * <p>
 * A subject in a model may be linked to more than one behavior.
 *
 * @see http://www.i2pm.net/standard-pass-ont#SubjectBehavior
 */
class MutableDoState extends MutablePASSProcessModelElement implements DoState, DoState.Mutable {


}
