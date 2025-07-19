package org.opensbpm.spass.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.opensbpm.spass.model.DoState;
import org.opensbpm.spass.model.PASSProcessModel;
import org.opensbpm.spass.model.PASSProcessModelElement;
import org.opensbpm.spass.model.SubjectBehavior;

import static org.hamcrest.Matchers.*;

public class ModelMatchers {

    public static TypeSafeMatcher<PASSProcessModelElement> hasId(String expectedId) {
        return new TypeSafeMatcher<>() {

            @Override
            protected boolean matchesSafely(PASSProcessModelElement model) {
                return is(expectedId).matches(model.getId());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a PASSProcessModel with id ")
                        .appendText(" equal to ")
                        .appendValue(expectedId);
            }

            @Override
            protected void describeMismatchSafely(PASSProcessModelElement model, Description mismatchDescription) {
                mismatchDescription.appendText("was ")
                        .appendValue(model.getId());
            }
        };
    }

    public static TypeSafeMatcher<PASSProcessModelElement> hasLabel(String expectedLabel) {
        return new TypeSafeMatcher<>() {

            @Override
            protected boolean matchesSafely(PASSProcessModelElement model) {
                return is(expectedLabel).matches(model.getLabel());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a PASSProcessModel with label ")
                        .appendText(" equal to ")
                        .appendValue(expectedLabel);
            }

            @Override
            protected void describeMismatchSafely(PASSProcessModelElement model, Description mismatchDescription) {
                mismatchDescription.appendText("was ")
                        .appendValue(model.getId());
            }
        };
    }

    public static TypeSafeMatcher<PASSProcessModel> hasElementCount(int expectedCount) {
        return new TypeSafeMatcher<>() {

            @Override
            protected boolean matchesSafely(PASSProcessModel model) {
                return model.getContains().size() == expectedCount;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a PASSProcessModel with ")
                        .appendValue(expectedCount)
                        .appendText(" elements");
            }

            @Override
            protected void describeMismatchSafely(PASSProcessModel model, Description mismatchDescription) {
                mismatchDescription.appendText("was ")
                        .appendValue(model.getContains().size());
            }
        };
    }

    public static TypeSafeMatcher<SubjectBehavior> isSubjectBehavior(String expectedId, String expectedLabel) {
        return new TypeSafeMatcher<>() {

            @Override
            protected boolean matchesSafely(SubjectBehavior model) {
                return allOf(
                        instanceOf(SubjectBehavior.class),
                        hasId(expectedId),
                        hasLabel(expectedLabel)
                ).matches(model);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a SubjectBehavior with id ")
                        .appendText(" equal to ")
                        .appendValue(expectedId)
                        .appendText(" and label equal to ")
                        .appendValue(expectedLabel);
            }

            @Override
            protected void describeMismatchSafely(SubjectBehavior model, Description mismatchDescription) {
                mismatchDescription.appendText(" id was ")
                        .appendValue(model.getId())
                        .appendText(" and label was ")
                        .appendValue(model.getLabel());
            }
        };
    }

    public static TypeSafeMatcher<DoState> isDoState(String expectedId, String expectedLabel) {
        return new TypeSafeMatcher<>() {

            @Override
            protected boolean matchesSafely(DoState model) {
                return allOf(
                        instanceOf(DoState.class),
                        hasId(expectedId),
                        hasLabel(expectedLabel)
                ).matches(model);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a DoState with id ")
                        .appendText(" equal to ")
                        .appendValue(expectedId)
                        .appendText(" and label equal to ")
                        .appendValue(expectedLabel);
            }

            @Override
            protected void describeMismatchSafely(DoState model, Description mismatchDescription) {
                mismatchDescription.appendText(" id was ")
                        .appendValue(model.getId())
                        .appendText(" and label was ")
                        .appendValue(model.getLabel());
            }
        };
    }

}
