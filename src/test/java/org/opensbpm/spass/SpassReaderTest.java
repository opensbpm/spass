package org.opensbpm.spass;

import org.junit.jupiter.api.Test;
import org.opensbpm.spass.model.DoState;
import org.opensbpm.spass.model.PASSProcessModel;
import org.opensbpm.spass.model.SubjectBehavior;

import java.io.InputStream;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.spass.matchers.ModelMatchers.*;

public class SpassReaderTest {

    @Test
    public void testReadSPassFile() throws Exception {
        //arrange
        InputStream inputStream = getClass().getResourceAsStream("/minimal.owl");
        assert inputStream != null;

        //act
        PASSProcessModel processModel = SPassReader.loadOwl(inputStream);

        //assert
        assertThat(processModel, hasId("AProcessModel"));
        assertThat(processModel, hasLabel("A Model"));
        assertThat(processModel, hasElementCount(1));

        Collection<SubjectBehavior> subjectBehaviors = processModel.getContains(SubjectBehavior.class);
        assertThat(subjectBehaviors, contains(
                isSubjectBehavior("ASubjectBehavior", "A SubjectBehavior")
        ));

        SubjectBehavior asubjectBehavior = subjectBehaviors.stream()
                .filter(subjectBehavior -> subjectBehavior.getId().equals("ASubjectBehavior"))
                .findFirst()
                .orElseThrow(AssertionError::new);

        Collection<DoState> doStates = asubjectBehavior.getContains(DoState.class);
        assertThat(doStates, contains(
                isDoState("ADoState", "A do State")
        ));

    }

}
