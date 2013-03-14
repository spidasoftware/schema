
JaySchema = require('jayschema')
js = new JaySchema(JaySchema.loaders.http)
describe 'project', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  path = require("path")

  it 'load the external project schema', ->
    logger.info "load the external project schema"
    data = fs.readFileSync "./v1/pm/project.schema"
    schema = JSON.parse(data)
    json = {"id":1,"draft":false}
    js.validate(json, schema, (errs)->
      if (errs)
        console.error(errs)
    )
    
