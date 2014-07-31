package com.mongodbdeploy.appliers;

import com.mongodbdeploy.ChangeScriptApplier;
import com.mongodbdeploy.database.QueryStatementSplitter;
import com.mongodbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.mongodbdeploy.database.changelog.QueryExecuter;
import com.mongodbdeploy.exceptions.ChangeScriptFailedException;
import com.mongodbdeploy.scripts.ChangeScript;
import com.mongodb.MongoException;
import java.util.List;

public class DirectToDbApplier implements ChangeScriptApplier {
    private final QueryExecuter queryExecuter;
    private final DatabaseSchemaVersionManager schemaVersionManager;
    private final QueryStatementSplitter splitter;

    public DirectToDbApplier(QueryExecuter queryExecuter, DatabaseSchemaVersionManager schemaVersionManager, QueryStatementSplitter splitter) {
        this.queryExecuter = queryExecuter;
        this.schemaVersionManager = schemaVersionManager;
        this.splitter = splitter;
    }

    public void apply(List<ChangeScript> changeScript) {
        begin();

        for (ChangeScript script : changeScript) {
            System.err.println("Applying " + script + "...");

            applyChangeScript(script);
            insertToSchemaVersionTable(script);

            commitTransaction();
        }
    }

    public void begin() {
        try {
            queryExecuter.setAutoCommit(false);
        } catch (MongoException e) {
            throw new RuntimeException(e);
        }
    }

    protected void applyChangeScript(ChangeScript script) {
        List<String> statements = splitter.split(script.getContent());

        for (int i = 0; i < statements.size(); i++) {
            String statement = statements.get(i);
            try {
                if (statements.size() > 1) {
                    System.err.println(" -> statement " + (i+1) + " of " + statements.size() + "...");
                }
                queryExecuter.executeQuery(statement);
            } catch (MongoException e) {
                throw new ChangeScriptFailedException(e, script, i+1, statement);
            }
        }
    }

    protected void insertToSchemaVersionTable(ChangeScript changeScript) {
        schemaVersionManager.recordScriptApplied(changeScript);
    }

    protected void commitTransaction() {
        try {
            queryExecuter.commit();
        } catch (MongoException e) {
            throw new RuntimeException();
        }
    }
}
