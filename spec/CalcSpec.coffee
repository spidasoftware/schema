describe 'calc', ->

  #Include some stuff.
  log4js = require('log4js')
  logger = log4js.getLogger()
  
  #Odd things happen if you set this in multiple places.
  logger.setLevel('INFO')

  fs = require('fs')
  path = require('path')
  tv4 = require('./tv4').tv4
  designSchema = "./public/v1/calc/design.schema"
  JaySchema = require('jayschema')
  #js = new JaySchema(JaySchema.loaders.http)
  js = new JaySchema()


  it 'Check point schema', ->
    schema = JSON.parse(fs.readFileSync "./v1/calc/point.schema")
    tv4.addSchema('https://raw.github.com/spidasoftware/schema/master/v1/calc/point.schema', schema)
    # console.log tv4.missing
    tv4.addSchema('https://raw.github.com/spidasoftware/schema/master/v1/general/measurable.schema', JSON.parse(fs.readFileSync "./v1/general/measurable.schema"))
    # console.log tv4.missing

    instance = {
      "id": "anchor1",
      "uuid": "682db637-0f31-4847-9cdf-25ba9613a75c",
      "distance": {
        "value": "10",
        "unit": "FOOT"
      }
      "direction": 360
    }

    valid = tv4.validate(instance, schema)
    console.log tv4.missing
    console.log valid
    
    # js.register(JSON.parse(fs.readFileSync "./v1/general/measurable.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/measurable.schema')
    # js.register(JSON.parse(fs.readFileSync "./v1/general/bearing.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/bearing.schema')
    # js.register(schema, 'https://raw.github.com/spidasoftware/schema/master/v1/calc/point.schema')

    # logger.info("Check point schema")
    # console.log "point schema", js.validate(instance, schema)

  # it 'check usage group schema', ->
  #   logger.info("check usage group schema")
  #   success = "PRIMARY"


  #   failure = "NONEXISTANT"
  #   testSchema = "./v1/calc/usage_group.schema"
  #   js.validate(success, testSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )
  #   js.validate(failure, testSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check empty design', ->
  #   logger.info("Checking empty pole")
  #   jsonString = fs.readFileSync("./examples/designs/empty_pole.json").toString()
  #   json = JSON.parse(jsonString)
  #   js.validate(json, designSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check everything pole', ->
  #   logger.info("Checking pole with everything")
  #   jsonString = fs.readFileSync("./examples/designs/one_of_everything.json", "utf8")
  #   json = JSON.parse(jsonString)
  #   js.validate(json, designSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check anchor guy pole', ->
  #   logger.info("Checking pole with anchor and guy")
  #   jsonString = fs.readFileSync("./examples/designs/anchor_guy.json", "utf8")
  #   json = JSON.parse(jsonString)
  #   js.validate(json, designSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check bisector pole', ->
  #   logger.info("Checking pole with bisector guy")
  #   jsonString = fs.readFileSync("./examples/designs/bisector.json", "utf8")
  #   json = JSON.parse(jsonString)
  #   js.validate(json, designSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check insulator pole', ->
  #   logger.info("Checking pole with insulator")
  #   jsonString = fs.readFileSync("./examples/designs/insulator.json", "utf8")
  #   json = JSON.parse(jsonString)
  #   js.validate(json, designSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check wire pole', ->
  #   logger.info("Checking pole with wire")
  #   jsonString = fs.readFileSync("./examples/designs/wire.json", "utf8")
  #   json = JSON.parse(jsonString)
  #   js.validate(json, designSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check crossarm pole', ->
  #   logger.info("Checking pole with crossarm.")
  #   jsonString = fs.readFileSync("./examples/designs/xarm.json", "utf8")
  #   json = JSON.parse(jsonString)
  #   js.validate(json, designSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check framing plan schema', ->
  #   logger.info ("check framing plan schema")
  #   jsonString = fs.readFileSync("./examples/designs/simple_framing_plan.json").toString()
  #   json = JSON.parse(jsonString)
  #   framingSchema = "./public/v1/calc/framing_plan.schema"
  #   js.validate(json, framingSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check minimal project schema', ->
  #   logger.info ("check minimal project schema")
  #   jsonString = fs.readFileSync("./examples/projects/minimal_project_with_gps.json").toString()
  #   json = JSON.parse(jsonString)
  #   framingSchema = "./public/v1/calc/calc_project.schema"
  #   js.validate(json, framingSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check full project schema', ->
  #   logger.info ("check full project schema")
  #   jsonString = fs.readFileSync("./examples/projects/full_project.json").toString()
  #   json = JSON.parse(jsonString)
  #   framingSchema = "./public/v1/calc/calc_project.schema"
  #   js.validate(json, framingSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

  # it 'check completely minimal project schema', ->
  #   logger.info ("check completely minimal project schema")
  #   jsonString = fs.readFileSync("./examples/projects/minimal_project_no_designs.json").toString()
  #   json = JSON.parse(jsonString)
  #   framingSchema = "./public/v1/calc/calc_project.schema"
  #   js.validate(json, framingSchema, (errs)->
  #     if (errs)
  #       console.error(errs)
  #   )

