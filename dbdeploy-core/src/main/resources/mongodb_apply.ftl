[#ftl]
[#-- @ftlvariable name="changeLogTableName" type="java.lang.String" --]
[#-- @ftlvariable name="delimiter" type="java.lang.String" --]
[#-- @ftlvariable name="separator" type="java.lang.String" --]
[#-- @ftlvariable name="scripts" type="java.util.List<com.mongodbdeploy.scripts.ChangeScript>" --]
[#list scripts as script]

// START CHANGE SCRIPT ${script}

${script.content}

db.${changeLogTableName}.insert(
  { _id : ${script.id?c},
    complete_dt : new Date(),
    applied_by : db.runCommand({connectionStatus : 1}).authInfo.authenticatedUsers[0].user,
    description : '${script.description}'
  }
) ${separator}${delimiter}

// END CHANGE SCRIPT ${script}

[/#list]
