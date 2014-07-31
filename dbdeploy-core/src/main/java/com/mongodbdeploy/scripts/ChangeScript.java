package com.mongodbdeploy.scripts;

import com.mongodbdeploy.exceptions.DbDeployException;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

public class ChangeScript implements Comparable {

    private final long id;
    private final File file;
    private final String description;
    private final String encoding;
    private static final String UNDO_MARKER = "//--@UNDO";

    public ChangeScript(long id) {
        this(id, "test");
    }

    public ChangeScript(long id, String description) {
        this.id = id;
        this.file = null;
        this.description = description;
        this.encoding = "UTF-8";
    }

    public ChangeScript(long id, File file, String encoding) {
        this.id = id;
        this.file = file;
        this.description = file.getName();
        this.encoding = encoding;
    }

    public File getFile() {
        return file;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int compareTo(Object o) {
        ChangeScript other = (ChangeScript) o;
        return Long.valueOf(this.id).compareTo(other.id);
    }

    @Override
    public String toString() {
        return "#" + id + ": " + description;
    }

    public String getContent() {
        return getFileContents(false);
    }

    public String getUndoContent() {
        return getFileContents(true);
    }

    private String getFileContents(boolean onlyAfterUndoMarker) {
        try {
            StringBuilder content = new StringBuilder();
            boolean foundUndoMarker = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

            try {
                for (;;) {
                    String str = reader.readLine();

                    if (str == null)
                        break;

                    if (str.trim().equals(UNDO_MARKER)) {
                        foundUndoMarker = true;
                        continue;
                    }

                    if (foundUndoMarker == onlyAfterUndoMarker) {
                        content.append(str);
                        content.append('\n');
                    }
                }
            } finally {
                reader.close();
            }

            return content.toString();
        } catch (IOException e) {
            throw new DbDeployException("Failed to read change script file", e);
        }
    }
}
