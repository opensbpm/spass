package org.opensbpm.spass;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opensbpm.spass.model.ClassModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class JavaGeneratorTest {

    @Test
    public void testGenerate(@TempDir File workDir) throws Exception {
        //arrange
        InputStream inputStream = getClass().getResourceAsStream("/standard_pass.owl");
        Path inputPath = workDir.toPath().resolve("standard_pass.owl");
        Files.copy(inputStream, inputPath);
        inputStream.close();

        File outputDirectory = workDir.toPath().resolve("output").toFile();
        String packageName = "apackage";


        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        IRI iri = IRI.create("http://example.com#DayTimeTimerTransitionCondition");
        OWLClass owlClass = dataFactory.getOWLClass(iri);

        Map<OWLClass,ClassModel> classModels = Map.of(
                owlClass,
                new ClassModel("DayTimeTimerTransitionCondition")
        );
        JavaGenerator javaGenerator = new JavaGenerator(outputDirectory, packageName);

        //act
        javaGenerator.generate(classModels);

        //assert
        File generatedFile = outputDirectory.toPath().resolve("apackage/api/DayTimeTimerTransitionCondition.java").toFile();
        assertThat("Generated file does not exist: " + generatedFile.getAbsolutePath(),
                generatedFile, anExistingFile());
    }
}
