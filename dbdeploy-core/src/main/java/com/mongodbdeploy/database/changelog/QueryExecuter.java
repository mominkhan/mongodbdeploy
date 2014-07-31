package com.mongodbdeploy.database.changelog;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBCollection;
import com.mongodb.ServerAddress;
import com.mongodb.MongoException;
import com.mongodb.CommandResult;
import com.mongodb.BasicDBObject;

import java.net.UnknownHostException;
import java.util.Arrays;

public class QueryExecuter {
    private final MongoClient mongo;
    private final DB db;
    private final String username;

    public QueryExecuter(String server, int port, String database, String username, String password) throws UnknownHostException {
        this.username = username;
        MongoCredential credential = MongoCredential.createMongoCRCredential(username, database, password.toCharArray());
        this.mongo = new MongoClient(new ServerAddress(server, port), Arrays.asList(credential));
        this.db = mongo.getDB(database);
    }

    public CommandResult executeQuery(String cmd) throws MongoException {
        CommandResult commandResult;
        db.requestStart();
        try {
            db.requestEnsureConnection();
            commandResult = db.doEval("function(){ \n" + cmd + "\n return db.getLastError() \n " + "}");
            Object retVal = commandResult.get("retval");
            if (retVal != null) {
                throw new MongoException("Error encountered while executing script:" + retVal);
            }
            if (!commandResult.ok()) {
                throw new MongoException("Error encountered while executing script.");
            }
        } finally {
            db.requestDone();
        }
        return commandResult;
    }

    public DBCursor executeSearchQuery(String collectionName) throws MongoException {
        DBCollection collection;
        DBCursor cursor;
        db.requestStart();
        try {
            db.requestEnsureConnection();
            collection = db.getCollection(collectionName);
            cursor = collection.find();
        } finally {
            db.requestDone();
        }
        return cursor;
    }

    public void execute(String cmd) throws MongoException {
        db.requestStart();
        try {
            db.requestEnsureConnection();
            db.eval(cmd);
        } finally {
            db.requestDone();
        }
    }

    public void executeInsertCommand(BasicDBObject dbObject, String changeLogCollectionName) throws MongoException {
        DBCollection collection;
        db.requestStart();
        try {
            db.requestEnsureConnection();
            collection = db.getCollection(changeLogCollectionName);
            collection.insert(dbObject);
        } finally {
            db.requestDone();
        }
    }

    public void execute(String cmd, Object... params) throws MongoException {
        BasicDBObject command = new BasicDBObject();
        db.requestStart();
        try {
            db.requestEnsureConnection();
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                command.append(Integer.toString(i + 1), param);
            }
            db.eval(cmd);
        } finally {
            db.requestDone();
        }
    }

    public void close() throws MongoException {
        db.requestDone();
        mongo.close();
    }

    public void setAutoCommit(boolean autoCommitMode) throws MongoException {
        // connection.setAutoCommit(autoCommitMode);
        // couldn't find an equivalent method in NoSQL based MongoDB,
        // so setting this as no-op for the time being
    }

    public void commit() throws MongoException {
        // connection.commit();
        // couldn't find an equivalent method in NoSQL based MongoDB,
        // so setting this as no-op for the time being
    }

    public String getDatabaseUsername() {
        return username;
    }
}
