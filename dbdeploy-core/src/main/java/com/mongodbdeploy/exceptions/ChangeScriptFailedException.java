package com.mongodbdeploy.exceptions;

import com.mongodbdeploy.scripts.ChangeScript;

import com.mongodb.MongoException;

public class ChangeScriptFailedException extends DbDeployException {
    private final ChangeScript script;
    private final int statement;
    private final String executedQuery;

    public ChangeScriptFailedException(MongoException cause, ChangeScript script,
                                       int statement, String executedQuery) {
        super(cause);
        this.script = script;
        this.statement = statement;
        this.executedQuery = executedQuery;
    }

    public ChangeScript getScript() {
        return script;
    }

    public String getExecutedQuery() {
        return executedQuery;
    }

    public int getStatement() {
        return statement;
    }

    @Override
    public String getMessage() {
        return "change script " + script +
                " failed while executing statement " + statement + ":\n"
                + executedQuery + "\n -> " + getCause().getMessage();
    }
}
