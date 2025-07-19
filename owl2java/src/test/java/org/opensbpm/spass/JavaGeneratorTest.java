package org.opensbpm.spass;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class JavaGeneratorTest {

    @Test
    public void testGenerateJava(@TempDir File workDir) throws Exception {
        //arrange
        InputStream inputStream = getClass().getResourceAsStream("/standard_pass.owl");
        Path inputPath = workDir.toPath().resolve("standard_pass.owl");
        Files.copy(inputStream, inputPath);
        inputStream.close();

        File outputDirectory = workDir.toPath().resolve("output").toFile();
        String packageName = "apackage";

        JavaGenerator javaGenerator = new JavaGenerator(inputPath.toFile(), outputDirectory, packageName);

        //act
        javaGenerator.generate();

        //assert
        File generatedFile = outputDirectory.toPath().resolve("apackage/DayTimeTimerTransitionCondition.java").toFile();
        assertThat("Generated file does not exist: " + generatedFile.getAbsolutePath(),
                generatedFile, anExistingFile());
    }
}
