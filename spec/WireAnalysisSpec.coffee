describe 'make sure the wire analysis service is valid.', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  logger.setLevel('INFO')
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()
  path = require("path")
  testUtils = require "./test_utils"

  #testUtils.supportedSchemas(["./v1/calc", "./v1/calc/tension"])

  it 'load the external project schema', ->
    logger.info "load the external project schema"
    data = fs.readFileSync "./v1/min/min_project.schema"
    schema = JSON.parse(data)

    
