package org.opensbpm.spass;

import org.junit.jupiter.api.Test;
import org.opensbpm.spass.reader.model.api.DoState;
import org.opensbpm.spass.reader.model.api.PASSProcessModel;
import org.opensbpm.spass.reader.model.api.SubjectBehavior;

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

        Collection<SubjectBehavior> subjectBehaviors = ModelUtils.getContains(processModel, SubjectBehavior.class);
        assertThat(subjectBehaviors, contains(
                isSubjectBehavior("ASubjectBehavior", "A SubjectBehavior")
        ));

        SubjectBehavior asubjectBehavior = subjectBehaviors.stream()
                .filter(subjectBehavior -> subjectBehavior.getHasModelComponentID().equals("ASubjectBehavior"))
                .findFirst()
                .orElseThrow(AssertionError::new);

        Collection<DoState> doStates =  ModelUtils.getContains(asubjectBehavior, DoState.class);
        assertThat(doStates, contains(
                isDoState("ADoState", "A do State")
        ));

    }

}
