package org.opensbpm.spass;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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



        File touch = new File(f, "touch.txt");

        FileWriter w = null;
        try {
            w = new FileWriter(touch);

            w.write("touch.java");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
