JaySchema = require('jayschema')
js = new JaySchema(JaySchema.loaders.http)

describe 'client', ->

  #Include some stuff.
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  designSchema = "./public/v1/calc/design.schema"

  #js.supportedSchemas(["./v1/general", "./v1/calc"])
  #js.supportedSchemas([])

  it 'check anchor', ->
    logger.info "check anchor"
    jsonString = fs.readFileSync("./examples/client/client_anchor_example.json").toString()
    json = JSON.parse(jsonString)
    js.validate(json, "./public/v1/calc/client/client_anchor.schema")

  it 'check crossarm', ->
    logger.info "check crossarm"
    jsonString = fs.readFileSync("./examples/client/client_xarm_example.json").toString()
    json = JSON.parse(jsonString)
    js.validate(json, "./public/v1/calc/client/client_crossarm.schema")

  it 'check equipment', ->
    logger.info "check equipment"
    jsonString = fs.readFileSync("./examples/client/client_equipment_example.json").toString()
    json = JSON.parse(jsonString)
    js.validate(json, "./public/v1/calc/client/client_equipment.schema")

  it 'check insulator', ->
    logger.info "check insulator"
    jsonString = fs.readFileSync("./examples/client/client_insulator_example.json").toString()
    json = JSON.parse(jsonString)
    js.validate(json, "./public/v1/calc/client/client_insulator.schema")

  it 'check pole', ->
    logger.info "check pole in client spec"
    jsonString = fs.readFileSync("./examples/client/client_pole_example.json").toString()
    json = JSON.parse(jsonString)
    js.validate(json, "./public/v1/calc/client/client_pole.schema")

  it 'check wire', ->
    logger.info "check wire"
    jsonString = fs.readFileSync("./examples/client/client_wire_example.json").toString()
    json = JSON.parse(jsonString)
    js.validate(json, "./public/v1/calc/client/client_wire.schema")
    
  it 'check wire bundle', ->
    logger.info "check wire bundle"
    jsonString = fs.readFileSync("./examples/client/client_bundle_example.json").toString()
    json = JSON.parse(jsonString)
    js.validate(json, "./public/v1/calc/client/client_bundle.schema")
    json.messenger = null
    js.validate(json, "./public/v1/calc/client/client_bundle.schema", false)

