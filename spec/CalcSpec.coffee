describe 'calc', ->

  #Include some stuff.
  log4js = require('log4js')
  logger = log4js.getLogger()
  logger.setLevel('ERROR')
  fs = require('fs')
  designSchema = "./public/v1/calc/design.schema"
  testUtils = require "./test_utils"

  testUtils.supportedSchemas(["./v1/calc","./v1/general"])

  it 'Check point schema', ->
    logger.info("Check point schema")
    json = {
      "id": "anchor1",
      "uuid": "682db637-0f31-4847-9cdf-25ba9613a75c",
      "distance": {
        "value": 10,
        "unit": "FOOT"
      }
      "direction": 360
    }

    testUtils.validate(json, "./v1/calc/point.schema", true)

  it 'check usage group schema', ->
    logger.info("check usage group schema")
    success = "PRIMARY"

    failure = "NONEXISTANT"
    testSchema = "./v1/calc/usage_group.schema"
    testUtils.validate(success, testSchema, true)
    testUtils.validate(failure, testSchema, false)

  it 'check empty design', ->
    logger.info("Checking empty pole")
    jsonString = fs.readFileSync("./examples/designs/empty_pole.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, designSchema)

  it 'check everything pole', ->
    logger.info("Checking pole with everything")
    jsonString = fs.readFileSync("./examples/designs/one_of_everything.json", "utf8")
    json = JSON.parse(jsonString)
    testUtils.validate(json, designSchema)

  it 'check anchor guy pole', ->
    logger.info("Checking pole with anchor and guy")
    jsonString = fs.readFileSync("./examples/designs/anchor_guy.json", "utf8")
    json = JSON.parse(jsonString)
    testUtils.validate(json, designSchema)

  it 'check bisector pole', ->
    logger.info("Checking pole with bisector guy")
    jsonString = fs.readFileSync("./examples/designs/bisector.json", "utf8")
    json = JSON.parse(jsonString)
    testUtils.validate(json, designSchema)

  it 'check insulator pole', ->
    logger.info("Checking pole with insulator")
    jsonString = fs.readFileSync("./examples/designs/insulator.json", "utf8")
    json = JSON.parse(jsonString)
    testUtils.validate(json, designSchema)

  it 'check wire pole', ->
    logger.info("Checking pole with wire")
    jsonString = fs.readFileSync("./examples/designs/wire.json", "utf8")
    json = JSON.parse(jsonString)
    testUtils.validate(json, designSchema)

  it 'check crossarm pole', ->
    logger.info("Checking pole with crossarm.")
    jsonString = fs.readFileSync("./examples/designs/xarm.json", "utf8")
    json = JSON.parse(jsonString)
    testUtils.validate(json, designSchema)

  it 'check framing plan schema', ->
    logger.info ("check framing plan schema")
    jsonString = fs.readFileSync("./examples/designs/simple_framing_plan.json").toString()
    json = JSON.parse(jsonString)
    framingSchema = "./public/v1/calc/framing_plan.schema"
    testUtils.validate(json, framingSchema)

  it 'check minimal project schema', ->
    logger.info ("check minimal project schema")
    jsonString = fs.readFileSync("./examples/projects/minimal_project_with_gps.json").toString()
    json = JSON.parse(jsonString)
    framingSchema = "./public/v1/calc/calc_project.schema"
    testUtils.validate(json, framingSchema)

  it 'check full project schema', ->
    logger.info ("check full project schema")
    jsonString = fs.readFileSync("./examples/projects/full_project.json").toString()
    json = JSON.parse(jsonString)
    framingSchema = "./public/v1/calc/calc_project.schema"
    testUtils.validate(json, framingSchema)

  it 'check completely minimal project schema', ->
    logger.info ("check completely minimal project schema")
    jsonString = fs.readFileSync("./examples/projects/minimal_project_no_designs.json").toString()
    json = JSON.parse(jsonString)
    framingSchema = "./public/v1/calc/calc_project.schema"
    testUtils.validate(json, framingSchema)

