package com.mongodbdeploy.mojo;

import com.mongodbdeploy.DbDeploy;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Maven goal for creating the apply and undo scripts.
 *
 * @goal db-scripts
 */
public class CreateDatabaseScriptsMojo extends AbstractDbDeployMojo {
    /**
     * The name of the script that dbdeploy will output. Include a full
     * or relative path.
     *
     * @parameter
     * @required
     */
    private File outputfile;

    /**
     * String representing our DBMS
     *
     * @parameter default-value="mongodb"
     * @required
     */
    private String dbms;

    /**
     * The name of the undo script that dbdeploy will output. Include a full
     * or relative path.
     *
     * @parameter
     * @required
     */
    private File undoOutputfile;

    /**
     * Directory for your template scripts, if not using built-in
     *
     * @parameter
     */
    private File templateDirectory;

    public void execute() throws MojoExecutionException {
        DbDeploy dbDeploy = getConfiguredDbDeploy();

        try {
            dbDeploy.go();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("mongodbdeploy change script create failed", e);
        }
    }

    @Override
    protected DbDeploy getConfiguredDbDeploy() {
        DbDeploy dbDeploy = super.getConfiguredDbDeploy();
        dbDeploy.setOutputfile(outputfile);
        dbDeploy.setUndoOutputfile(undoOutputfile);
        dbDeploy.setDbms(dbms);

        if (templateDirectory != null) {
            dbDeploy.setTemplatedir(templateDirectory);
        }

        return dbDeploy;
    }
}
