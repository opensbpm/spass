import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "owl2java", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        threadSafe = true,
        requiresProject = true)
public class Owl2JavaGenMojo extends AbstractMojo {
    /**
     * The path to the OWL file to be processed.
     *
     * @parameter property="owlFilePath"
     * @required
     */
    private String owlFilePath;

    /**
     * The output directory for the generated Java files.
     *
     * @parameter property="outputDirectory"
     * @required
     */
    private String outputDirectory;

    @Override
    public void execute() {
        // Logic to process the OWL file and generate Java code
        getLog().info("Processing OWL file: " + owlFilePath);
        getLog().info("Output directory: " + outputDirectory);
        // Add your code generation logic here
    }
}
