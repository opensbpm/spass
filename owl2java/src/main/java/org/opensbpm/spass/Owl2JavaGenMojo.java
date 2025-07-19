package org.opensbpm.spass;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

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
            new JavaGenerator(inputFile, f,packageName).generate();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
