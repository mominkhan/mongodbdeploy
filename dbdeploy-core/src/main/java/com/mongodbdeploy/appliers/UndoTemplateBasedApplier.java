package com.mongodbdeploy.appliers;

import com.mongodbdeploy.database.DelimiterType;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class UndoTemplateBasedApplier extends TemplateBasedApplier {
    public UndoTemplateBasedApplier(Writer writer, String syntax,
                                    String changeLogTableName, String delimiter, DelimiterType delimiterType, File templateDirectory) throws IOException {
        super(writer, syntax, changeLogTableName, delimiter, delimiterType, templateDirectory);
    }

    @Override
    protected String getTemplateQualifier() {
        return "undo";
    }
}