describe 'make sure the wire analysis service is valid.', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()
  path = require("path")
  testUtils = require "./test_utils"

  #testUtils.supportedSchemas(["./v1/calc", "./v1/calc/tension"])

  it 'should validate against the scervice schema', ->
    jsonString = fs.readFileSync("./examples/designs/empty_pole.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json ,"./v1/general/service_method.schema")

    
