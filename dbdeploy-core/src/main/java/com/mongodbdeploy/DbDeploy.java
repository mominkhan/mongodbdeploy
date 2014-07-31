package com.mongodbdeploy;

import com.mongodbdeploy.appliers.DirectToDbApplier;
import com.mongodbdeploy.appliers.TemplateBasedApplier;
import com.mongodbdeploy.appliers.UndoTemplateBasedApplier;
import com.mongodbdeploy.database.DelimiterType;
import com.mongodbdeploy.database.LineEnding;
import com.mongodbdeploy.database.QueryStatementSplitter;
import com.mongodbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.mongodbdeploy.database.changelog.QueryExecuter;
import com.mongodbdeploy.exceptions.UsageException;
import com.mongodbdeploy.scripts.ChangeScriptRepository;
import com.mongodbdeploy.scripts.DirectoryScanner;

import java.io.File;
import java.io.PrintWriter;


public class DbDeploy {
    private String server = "localhost";
    private int port = 27017;
    private String database;
    private String userid;
    private String password;
    private String encoding = "UTF-8";
    private File scriptdirectory;
    private File outputfile;
    private File undoOutputfile;
    private LineEnding lineEnding = LineEnding.platform;
    private String dbms = "mongodb";
    private Long lastChangeToApply = Long.MAX_VALUE;
    private String changeLogTableName = "changelog";
    private String delimiter = ";";
    private DelimiterType delimiterType = DelimiterType.normal;
    private File templatedir;

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScriptdirectory(File scriptdirectory) {
        this.scriptdirectory = scriptdirectory;
    }

    public void setOutputfile(File outputfile) {
        this.outputfile = outputfile;
    }

    public void setDbms(String dbms) {
        this.dbms = dbms;
    }

    public void setLastChangeToApply(Long lastChangeToApply) {
        this.lastChangeToApply = lastChangeToApply;
    }

    public void setUndoOutputfile(File undoOutputfile) {
        this.undoOutputfile = undoOutputfile;
    }

    public void setChangeLogTableName(String changeLogTableName) {
        this.changeLogTableName = changeLogTableName;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setLineEnding(LineEnding lineEnding) {
        this.lineEnding = lineEnding;
    }

    public void go() throws Exception {
        System.err.println(getWelcomeString());

        validate();

        QueryExecuter queryExecuter = new QueryExecuter(server, port, database, userid, password);

        DatabaseSchemaVersionManager databaseSchemaVersionManager =
                new DatabaseSchemaVersionManager(queryExecuter, changeLogTableName);

        ChangeScriptRepository changeScriptRepository =
                new ChangeScriptRepository(new DirectoryScanner(encoding).getChangeScriptsForDirectory(scriptdirectory));

        ChangeScriptApplier doScriptApplier;

        if (outputfile != null) {
            doScriptApplier = new TemplateBasedApplier(
                    new PrintWriter(outputfile, encoding), dbms,
                    changeLogTableName, delimiter, delimiterType, getTemplatedir());
        } else {
            QueryStatementSplitter splitter = new QueryStatementSplitter();
            splitter.setDelimiter(getDelimiter());
            splitter.setDelimiterType(getDelimiterType());
            splitter.setOutputLineEnding(lineEnding);
            doScriptApplier = new DirectToDbApplier(queryExecuter, databaseSchemaVersionManager, splitter);
        }

        ChangeScriptApplier undoScriptApplier = null;

        if (undoOutputfile != null) {
            undoScriptApplier = new UndoTemplateBasedApplier(
                    new PrintWriter(undoOutputfile), dbms, changeLogTableName, delimiter, delimiterType, templatedir);

        }

        Controller controller = new Controller(changeScriptRepository, databaseSchemaVersionManager, doScriptApplier, undoScriptApplier);

        controller.processChangeScripts(lastChangeToApply);

        queryExecuter.close();
    }

    private void validate() throws UsageException {
        checkForRequiredParameter(userid, "userid");
        checkForRequiredParameter(scriptdirectory, "dir");

        if (scriptdirectory == null || !scriptdirectory.isDirectory()) {
            throw new UsageException("Script directory must point to a valid directory");
        }
    }

    private void checkForRequiredParameter(String parameterValue, String parameterName) throws UsageException {
        if (parameterValue == null || parameterValue.length() == 0) {
            UsageException.throwForMissingRequiredValue(parameterName);
        }
    }

    private void checkForRequiredParameter(Object parameterValue, String parameterName) throws UsageException {
        if (parameterValue == null) {
            UsageException.throwForMissingRequiredValue(parameterName);
        }
    }

    public String getUserid() {
        return userid;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getPassword() {
        return password;
    }

    public File getScriptdirectory() {
        return scriptdirectory;
    }

    public File getOutputfile() {
        return outputfile;
    }

    public File getUndoOutputfile() {
        return undoOutputfile;
    }

    public String getDbms() {
        return dbms;
    }

    public Long getLastChangeToApply() {
        return lastChangeToApply;
    }

    public void setTemplatedir(File templatedir) {
        this.templatedir = templatedir;
    }

    public File getTemplatedir() {
        return templatedir;
    }

    public String getChangeLogTableName() {
        return changeLogTableName;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public DelimiterType getDelimiterType() {
        return delimiterType;
    }


    public void setDelimiterType(DelimiterType delimiterType) {
        this.delimiterType = delimiterType;
    }

    public String getWelcomeString() {
        String version = getClass().getPackage().getImplementationVersion();
        return "mongodbdeploy " + version;
    }

    public String getEncoding() {
        return encoding;
    }

    public LineEnding getLineEnding() {
        return lineEnding;
    }
}
