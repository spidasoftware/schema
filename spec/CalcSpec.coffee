describe 'in the calc folder', ->

  #Include some stuff.
  log4js = require('log4js')
  logger = log4js.getLogger()
  
  #Odd things happen if you set this in multiple places.
  logger.setLevel('INFO')

  fs = require('fs')
  designSchema = "./v1/calc/design.schema"
  testUtils = require "./test_utils"
  testUtils.loadSchemasInFolder("/v1/general")
  testUtils.loadSchemasInFolder("/v1/calc")

  describe 'and the general object schemas', ->
    it 'should validate an point object', ->
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

    it 'should validate an usage group ', ->
      logger.debug("check usage group schema")
      success = "PRIMARY"
      failure = "NONEXISTANT"
      testSchema = "./v1/calc/usage_group.schema"
      testUtils.validate(success, testSchema, true)
      testUtils.validate(failure, testSchema, false)

    it 'should validate an framing plan', ->
      logger.debug ("check framing plan schema")
      jsonString = fs.readFileSync("./examples/designs/simple_framing_plan.json").toString()
      json = JSON.parse(jsonString)
      framingSchema = "./v1/calc/framing_plan.schema"
      testUtils.validate(json, framingSchema, true)

  describe 'and the design schema base', ->
    it 'should validate an empty design', ->
      logger.debug("Checking empty pole")
      jsonString = fs.readFileSync("./examples/designs/empty_pole.json").toString()
      json = JSON.parse(jsonString)
      testUtils.validate(json, designSchema, true)

    it 'should validate an everything pole', ->
      logger.debug("Checking pole with everything")
      jsonString = fs.readFileSync("./examples/designs/one_of_everything.json", "utf8")
      json = JSON.parse(jsonString)
      testUtils.validate(json, designSchema, true)

    it 'should validate an anchor guy pole', ->
      logger.debug("Checking pole with anchor and guy")
      jsonString = fs.readFileSync("./examples/designs/anchor_guy.json", "utf8")
      json = JSON.parse(jsonString)
      testUtils.validate(json, designSchema, true)

    it 'should validate an bisector pole', ->
      #logger.debug("Checking pole with bisector guy")
      jsonString = fs.readFileSync("./examples/designs/bisector.json", "utf8")
      json = JSON.parse(jsonString)
      testUtils.validate(json, designSchema, true)

    it 'should validate an insulator pole', ->
      logger.debug("Checking pole with insulator")
      jsonString = fs.readFileSync("./examples/designs/insulator.json", "utf8")
      json = JSON.parse(jsonString)
      testUtils.validate(json, designSchema, true)

    it 'should validate an wire pole', ->
      logger.debug("Checking pole with wire")
      jsonString = fs.readFileSync("./examples/designs/wire.json", "utf8")
      json = JSON.parse(jsonString)
      testUtils.validate(json, designSchema, true)

    it 'should validate an crossarm pole', ->
      logger.debug("Checking pole with crossarm.")
      jsonString = fs.readFileSync("./examples/designs/xarm.json", "utf8")
      json = JSON.parse(jsonString)
      testUtils.validate(json, designSchema, true)

  describe 'and the project schema base', ->
    it 'should validate an minimal project ', ->
      logger.debug ("check minimal project schema")
      jsonString = fs.readFileSync("./examples/projects/minimal_project_with_gps.json").toString()
      json = JSON.parse(jsonString)
      framingSchema = "./v1/calc/calc_project.schema"
      testUtils.validate(json, framingSchema, true)

    it 'should validate an full project ', ->
      logger.debug ("check full project schema")
      jsonString = fs.readFileSync("./examples/projects/full_project.json").toString()
      json = JSON.parse(jsonString)
      framingSchema = "./v1/calc/calc_project.schema"
      testUtils.validate(json, framingSchema, true)

    it 'should validate an completely minimal project ', ->
      logger.debug ("check completely minimal project schema")
      jsonString = fs.readFileSync("./examples/projects/minimal_project_no_designs.json").toString()
      json = JSON.parse(jsonString)
      framingSchema = "./v1/calc/calc_project.schema"
      testUtils.validate(json, framingSchema, true)

