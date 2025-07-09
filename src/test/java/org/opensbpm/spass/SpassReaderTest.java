package org.opensbpm.spass;

import org.junit.jupiter.api.Test;
import org.opensbpm.spass.model.ProcessModel;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class SpassReaderTest {

    @Test
    public void testReadSPassFile() {
        //arrange
        InputStream inputStream = getClass().getResourceAsStream("/minimal.owl");
        assert inputStream != null;

        //act
        ProcessModel processModel = new SPassReader().read(inputStream);

        //assert
        assertThat(processModel, notNullValue());
    }
}
