package org.opensbpm.spass;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opensbpm.spass.model.ClassModel;
import org.semanticweb.owlapi.model.OWLClass;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class OwlReaderTest {

    @Test
    public void testParse(@TempDir File workDir) throws Exception {
        //arrange
        InputStream inputStream = getClass().getResourceAsStream("/standard_pass.owl");
        Path inputPath = workDir.toPath().resolve("standard_pass.owl");
        Files.copy(inputStream, inputPath);
        inputStream.close();

        File outputDirectory = workDir.toPath().resolve("output").toFile();
        String packageName = "apackage";

        OwlReader owlReader = new OwlReader();

        //act
        Map<OWLClass, ClassModel> classModels = owlReader.parse(inputPath.toFile());

        //assert
        assertThat(classModels.values(), not(empty()));
    }
}
