describe 'in the client folder', ->

  #Include some stuff.
  log4js = require('log4js')
  logger = log4js.getLogger()
  logger.setLevel('INFO')

  fs = require('fs')
  designSchema = "./v1/spidacalc/design.schema"
  testUtils = require "./test_utils"
  testUtils.loadSchemasInFolder("/v1/general")
  testUtils.loadSchemasInFolder("/v1/spidacalc")
  testUtils.loadSchemasInFolder("/v1/spidacalc/client")

  it 'should validate a simple anchor', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_anchor_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_anchor.schema", true)

  it 'should validate a simple crossarm', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_xarm_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_crossarm.schema", true)

  it 'should validate a simple equipment', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_equipment_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_equipment.schema", true)

  it 'should validate a simple insulator', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_insulator_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_insulator.schema", true)

  it 'should validate a simple pole', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_pole_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_pole.schema", true)

  it 'should validate a simple wire', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_wire_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_wire.schema", true)
    
  it 'should validate a simple wire bundle', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_bundle_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_bundle.schema", true)

  it 'should not validate a simple invalid wire bundle', ->
    jsonString = fs.readFileSync("./spec/fixtures/spidacalc/client/client_bundle_invalid_example.json").toString()
    json = JSON.parse(jsonString)
    testUtils.validate(json, "./v1/spidacalc/client/client_bundle.schema", false)

