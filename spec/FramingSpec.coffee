describe 'calc', ->

  #Include some stuff.
  log4js = require('log4js')
  logger = log4js.getLogger()
  logger.setLevel('INFO')
  fs = require('fs')

  testUtils = require "./test_utils"

  testUtils.supportedSchemas(["./v1/general", "./v1/calc"])

  it 'check framing plan schema', ->
    logger.info ("check framing plan schema")
    jsonString = fs.readFileSync("./examples/designs/simple_framing_plan.json").toString()
    json = JSON.parse(jsonString)
    framingSchema = "./public/v1/calc/framing_plan.schema"
    testUtils.validate(json, framingSchema)