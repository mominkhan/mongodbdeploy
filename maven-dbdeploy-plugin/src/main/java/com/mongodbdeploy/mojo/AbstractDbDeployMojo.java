package com.mongodbdeploy.mojo;

import com.mongodbdeploy.DbDeploy;
import com.mongodbdeploy.database.DelimiterType;
import com.mongodbdeploy.database.LineEnding;
import org.apache.maven.plugin.AbstractMojo;

import java.io.File;

/**
 * Abstract class that all dbdeploy database goals should extend.
 */
public abstract class AbstractDbDeployMojo extends AbstractMojo {
    /**
     * Full or relative path to the directory containing the delta scripts.
     *
     * @parameter expression="${mongodbdeploy.scriptdirectory}" default-value="${project.src.directory}/main/scripts"
     * @required
     */
    protected File scriptdirectory;

    /**
     * Encoding to use for change scripts and output files.
     *
     * @parameter expression="${mongodbdeploy.encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;

    /**
     * Specifies the database server that the deltas are to be applied to.
     * If not supplied defaults to "localhost"
     *
     * @parameter expression="${mongodbdeploy.server}" default-value="localhost"
     */
    protected String server;

    /**
     * Specifies the port of the database that the deltas are to be applied to.
     * If not supplied defaults to "27017"
     *
     * @parameter expression="${mongodbdeploy.port}" default-value="27017"
     */
    protected int port;

    /**
     * Specifies the name of the database that the deltas are to be applied to.
     *
     * @parameter expression="${mongodbdeploy.database}"
     * @required
     */
    protected String database;

    /**
     * The ID of a dbms user who has permissions to select from the schema
     * version table.
     *
     * @parameter expression="${mongodbdeploy.userid}"
     * @required
     */
    protected String userid;

    /**
     * The password of the dbms user who has permissions to select from the
     * schema version table.
     *
     * @parameter expression="${mongodbdeploy.password}"
     */
    protected String password;

    /**
     * The name of the changelog table to use. Useful if you need to separate
     * DDL and DML when deploying to replicated environments. If not supplied
     * defaults to "changelog"
     *
     * @parameter expression="${mongodbdeploy.changeLogTableName}"
     */
    protected String changeLogTableName;

    /**
     * Delimiter to use to separate scripts into statements, if dbdeploy will
     * apply the scripts for you i.e. you haven't specified outputfile. Default ;
     *
     * @parameter expression="${mongodbdeploy.delimiter}"
     */
    protected String delimiter;

    /**
     * Either normal: split on delimiter wherever it occurs or row  only split
     * on delimiter if it features on a line by itself. Default normal.
     *
     * @parameter expression="${mongodbdeploy.delimiterType}"
     */
    protected String delimiterType;

    /**
     * Line ending to separate individual statement lines when applying directly
     * to the database. Can be platform (the default line ending for the current platform),
     * cr, crlf or lf. Default platform.
     *
     * @parameter expression="${mongodbdeploy.lineEnding}"
     */
    protected String lineEnding;

    /**
     * The highest numbered delta script to apply.
     *
     * @parameter expression="${mongodbdeploy.lastChange}"
     */
    protected Long lastChangeToApply;

    protected DbDeploy getConfiguredDbDeploy() {
        DbDeploy dbDeploy = new DbDeploy();
        dbDeploy.setScriptdirectory(scriptdirectory);
        dbDeploy.setServer(server);
        dbDeploy.setPort(port);
        dbDeploy.setDatabase(database);
        dbDeploy.setUserid(userid);
        dbDeploy.setPassword(password);

        if (encoding != null) {
            dbDeploy.setEncoding(encoding);
        }

        if (lastChangeToApply != null) {
            dbDeploy.setLastChangeToApply(lastChangeToApply);
        }

        if (changeLogTableName != null) {
            dbDeploy.setChangeLogTableName(changeLogTableName);
        }

        if (delimiter != null) {
            dbDeploy.setDelimiter(delimiter);
        }

        if (delimiterType != null) {
            dbDeploy.setDelimiterType(DelimiterType.valueOf(delimiterType));
        }

        if (lineEnding != null) {
            dbDeploy.setLineEnding(LineEnding.valueOf(lineEnding));
        }

        return dbDeploy;
    }
}
