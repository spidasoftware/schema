describe 'project', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()
  path = require("path")
  testUtils = require "./test_utils"

  it 'load the external project schema', ->
    logger.debug "load the external project schema"
    data = fs.readFileSync "./v1/spidamin/project/project.schema"
    schema = JSON.parse(data)
    json = {"id":"1","draft":false}
    report = env.validate(json, schema)
    
