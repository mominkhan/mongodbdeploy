package com.mongodbdeploy.mojo;

import com.mongodbdeploy.scripts.ChangeScriptCreator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Maven goal for creating a new timestamped dbdeploy change script.
 *
 * @goal change-script
 */
public class CreateChangeScriptMojo extends AbstractMojo {
    /**
     * Name suffix for the file that will be created (e.g. add_email_to_user_table).
     *
     * @parameter expression="${mongodbdeploy.script.name}" default-value="new_change_script"
     * @required
     */
    private String name;

    /**
     * Directory where change scripts reside.
     *
     * @parameter expression="${mongodbdeploy.scriptdirectory}" default-value="${project.src.directory}/main/scripts"
     * @required
     */
    private File scriptdirectory;

    public void execute() throws MojoExecutionException {
        try {
            final ChangeScriptCreator changeScriptCreator = getConfiguredChangeScriptCreator();
            final File newChangeScript = changeScriptCreator.go();

            getLog().info("Created new change script:\n\t" + newChangeScript.getAbsolutePath());
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("create change script failed", e);
        }
    }

    private ChangeScriptCreator getConfiguredChangeScriptCreator() {
        final ChangeScriptCreator changeScriptCreator = new ChangeScriptCreator();
        changeScriptCreator.setScriptDescription(name);
        changeScriptCreator.setScriptDirectory(scriptdirectory);

        return changeScriptCreator;
    }
}
