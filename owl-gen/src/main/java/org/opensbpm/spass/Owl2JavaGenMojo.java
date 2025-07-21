package org.opensbpm.spass;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.opensbpm.spass.model.ClassModel;
import org.semanticweb.owlapi.model.OWLClass;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Goal which creates Java files based on OWL input.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class Owl2JavaGenMojo extends AbstractMojo {
    /**
     * Input OWL file.
     */
    @Parameter(name = "inputFile", required = true)
    private File inputFile;

    /**
     * Location of the file.
     */
    @Parameter(
            name = "outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources/owl2java",
            required = true)
    private File outputDirectory;

    @Parameter
    private String packageName;

    public void execute()
            throws MojoExecutionException {
        File f = outputDirectory;
        if (!f.exists()) {
            f.mkdirs();
        }

        try {
            Map<OWLClass, ClassModel> classModels = new OwlReader().parse(inputFile);
            new JavaGenerator(outputDirectory, packageName).generate(classModels);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
