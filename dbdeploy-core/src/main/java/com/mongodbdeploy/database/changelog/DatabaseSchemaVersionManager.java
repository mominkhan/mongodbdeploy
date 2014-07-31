package com.mongodbdeploy.database.changelog;

import com.mongodbdeploy.AppliedChangesProvider;
import com.mongodbdeploy.exceptions.SchemaVersionTrackingException;
import com.mongodbdeploy.scripts.ChangeScript;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is responsible for all interaction with the changelog table
 */
public class DatabaseSchemaVersionManager implements AppliedChangesProvider {

    private final QueryExecuter queryExecuter;
    private final String changeLogTableName;

    public DatabaseSchemaVersionManager(QueryExecuter queryExecuter, String changeLogTableName) {
        this.queryExecuter = queryExecuter;
        this.changeLogTableName = changeLogTableName;
    }

    public List<Long> getAppliedChanges() {
        try {
            BasicDBObject orderBy = new BasicDBObject();
            orderBy.put("_id", 1);
            DBCursor cursor = queryExecuter.executeSearchQuery(changeLogTableName).sort(orderBy);

            List<Long> changeNumbers = new ArrayList<Long>();
            try {
                while (cursor.hasNext()) {
                    System.out.println(cursor.next());
                    System.out.println("Keys: " + cursor.curr().get("_id"));
                    String id = cursor.curr().get("_id").toString();
                    float f = Float.parseFloat(id);
                    long num = Math.round(f);
                    changeNumbers.add(num);
                }
            } finally {
                cursor.close();
            }

            return changeNumbers;
        } catch (MongoException e) {
            throw new SchemaVersionTrackingException("Could not retrieve change log from database because: "
                    + e.getMessage(), e);
        }
    }

    public String getChangelogDeleteSql(ChangeScript script) {
        BasicDBObject dbObject = new BasicDBObject("_id",script.getId());
        return String.format(
                "db.%s.remove({_id: %d})",
                changeLogTableName,
                script.getId());
    }

    public void recordScriptApplied(ChangeScript script) {
        try {
            BasicDBObject dbObject = new BasicDBObject("_id", script.getId())
                    .append("complete_dt", new Date())
                    .append("applied_by", queryExecuter.getDatabaseUsername())
                    .append("description", script.getDescription());

            queryExecuter.executeInsertCommand(dbObject, changeLogTableName);
        } catch (MongoException e) {
            throw new SchemaVersionTrackingException("Could not update change log because: "
                    + e.getMessage(), e);
        }
    }
}
