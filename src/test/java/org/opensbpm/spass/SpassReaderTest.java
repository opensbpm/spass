package org.opensbpm.spass;

import org.junit.jupiter.api.Test;
import org.opensbpm.spass.model.ProcessModel;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpassReaderTest {

    @Test
    public void testReadSPassFile() throws Exception {
        //arrange
        InputStream inputStream = getClass().getResourceAsStream("/minimal.owl");
        assert inputStream != null;

        //act
        ProcessModel processModel = new SPassReader().read(inputStream);

        //assert
        assertThat(processModel, hasProperty("id", is("AProcessModel")));
        assertThat(processModel, hasProperty("label", is("A Model")));
    }

}
