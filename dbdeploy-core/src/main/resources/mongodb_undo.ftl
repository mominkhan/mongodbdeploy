[#ftl]
[#-- @ftlvariable name="changeLogTableName" type="java.lang.String" --]
[#-- @ftlvariable name="delimiter" type="java.lang.String" --]
[#-- @ftlvariable name="separator" type="java.lang.String" --]
[#-- @ftlvariable name="scripts" type="java.util.List<com.mongodbdeploy.scripts.ChangeScript>" --]
[#list scripts as script]

// START UNDO OF CHANGE SCRIPT ${script}

${script.undoContent}

db.${changeLogTableName}.remove( { _id : ${script.id?c} } ) ${separator}${delimiter}

// END UNDO OF CHANGE SCRIPT ${script}

[/#list]
