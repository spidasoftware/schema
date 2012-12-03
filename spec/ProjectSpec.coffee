describe 'project', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()
  path = require("path")
  testUtils = require "./test_utils"

  #testUtils.supportedSchemas(["./v1/general", "./v1/min", "./v1/asset"])

  it 'load the external project schema', ->
    logger.info "load the external project schema"
    data = fs.readFileSync "./v1/min/min_project.schema"
    schema = JSON.parse(data)
    json = {"name":"test name"}
    report = env.validate(json, schema)
    #testUtils.validate(json, schema)
    #expect(report.errors.length).toBe(0)
    #if (report.errors.length != 0) 
    #  logger.info JSON.stringify(report.errors, null, 2)    
    
