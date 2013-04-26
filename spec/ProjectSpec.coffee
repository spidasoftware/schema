describe 'project', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()
  path = require("path")
  testUtils = require "./test_utils"
  testUtils.loadSchemasInFolder("/v1/general")
  testUtils.loadSchemasInFolder("/v1/spidamin/project")

  it 'load the validate a very simple project schema', ->
    logger.debug "load the external project schema"
    json = {"id":1,"flowId":1}
    testSchema = "./v1/spidamin/project/project.schema"
    testUtils.validate(json, testSchema, true)

  it 'load the external project schema', ->
    logger.debug "load the external project schema"
    jsonString = fs.readFileSync("./spec/fixtures/spidamin/project.json").toString()
    testSchema = "./v1/spidamin/project/project.schema"
    json = JSON.parse(jsonString)
    testUtils.validate(json, testSchema, true)
    
