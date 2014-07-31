package com.mongodbdeploy.mojo;

import com.mongodbdeploy.DbDeploy;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Maven goal for applying dbdeploy change scripts directly to the database.
 *
 * @goal update
 */
public class UpdateDatabaseMojo extends AbstractDbDeployMojo {
    public void execute() throws MojoExecutionException {
        DbDeploy dbDeploy = getConfiguredDbDeploy();

        try {
            dbDeploy.go();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("mongodbdeploy update failed", e);
        }
    }
}
