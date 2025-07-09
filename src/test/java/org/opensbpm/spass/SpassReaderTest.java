package org.opensbpm.spass;

import org.junit.jupiter.api.Test;
import org.opensbpm.spass.model.ProcessModel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class SpassReaderTest {

    @Test
    public void testReadSpassFile() {
        //arrange

        //act
        ProcessModel processModel = new SPassReader().readFile("src/test/resources/standard_PASS_ont_dev.owl");

        //assert
        assertThat(processModel, notNullValue());
    }
}
