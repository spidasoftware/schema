log4js = require('log4js')
logger = log4js.getLogger()
fs = require('fs')
JSV = require("JSV").JSV
path = require("path")
env = JSV.createEnvironment("json-schema-draft-03")


loadSchemasInFolder = (folder) ->
  files = fs.readdirSync(folder)
  logger.debug "loading schemas in #{folder}"
  for file in files
    do (file) ->
      if path.extname(file) is ".schema"
        logger.info "loading schema for file #{file}"
        schemaData = fs.readFileSync("#{folder}/#{file}")
        env.createSchema( schemaData, true, path.basename(file, ".schema")  )

validate = (json, schemaFile, success=true, supportedSchemasArray=[]) ->
  for schema in supportedSchemasArray
    logger.debug "  Loading: "+schema
    loadSchemasInFolder(schema)
  logger.debug("json string to validate.")
  logger.debug(JSON.stringify(json, null, 2))
  data = fs.readFileSync(schemaFile)
  schema = JSON.parse(data)
  report = env.validate(json, schema)
  if success
    expect(report.errors.length).toBe(0)
    if report.errors.length>0
      logger.error "Errors during validation"
      for error in report.errors
        logger.error JSON.stringify(error, null, 2)
  else
    expect(report.errors.length).not.toBe(0)
    if report.errors.length==0
      logger.error "There were no errors, but we expected some."

module.exports.validate = validate
module.exports.loadSchemasInFolder = loadSchemasInFolder
