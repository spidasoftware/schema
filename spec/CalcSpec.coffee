describe 'calc spec:', ->

  #Include some stuff.
  log4js = require('log4js')
  fs = require('fs')
  JaySchema = require('jayschema')

  logger = log4js.getLogger()  
  logger.setLevel('INFO')

  describe 'we should check the various basic calc objects', ->  
    js = new JaySchema()
    it 'Check point schema', ->
      schema = JSON.parse(fs.readFileSync "./v1/calc/point.schema")
      instance = {
        "id": "anchor1",
        "uuid": "682db637-0f31-4847-9cdf-25ba9613a75c",
        "distance": {
          "value": 10,
          "unit": "FOOT"
        }
        "direction": 360
      }
      
      js.register(JSON.parse(fs.readFileSync "./v1/general/measurable.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/measurable.schema')
      js.register(JSON.parse(fs.readFileSync "./v1/general/bearing.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/bearing.schema')
      js.register(schema, 'https://raw.github.com/spidasoftware/schema/master/v1/calc/point.schema')

      # logger.info("Check point schema")
      errors = js.validate(instance, schema)
      if(errors.length>0)
        console.log errors
      expect(errors.length).toBe(0)
      instance = {
        "id": "anchor1",
        "uuid": "682db637-0f31-4847-9cdf-25ba9613a75c",
        "addtional":"additonal"
      }

      errors = js.validate(instance, schema)
      expect(errors.length).toBe(2)
  
    it 'check usage group schema', ->
      success = "PRIMARY"
      failure = "NONEXISTANT"
      testSchema = JSON.parse(fs.readFileSync "./v1/calc/usage_group.schema")
      js.register(testSchema, 'https://raw.github.com/spidasoftware/schema/master/v1/calc/usage_group.schema')
      errors = js.validate(success, testSchema)
      expect(errors.length).toBe(0)
      errors = js.validate(failure, testSchema)
      expect(errors.length).toBe(1)

  describe 'we should check the various parts of the design', ->  
    js = new JaySchema()
    designSchema = JSON.parse(fs.readFileSync "./v1/calc/design.schema")
    js.register(JSON.parse(fs.readFileSync "./v1/general/bearing.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/bearing.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/general/owner.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/owner.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/general/measurable.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/measurable.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/point.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/point.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/environment.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/environment.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/attachment.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/attachment.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/damage_type.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/damage_type.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/client_pole_reference.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/client_pole_reference.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/client_equipment_reference.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/client_equipment_reference.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/client_wire_reference.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/client_wire_reference.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/wep_type.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/wep_type.schema')
    js.register(designSchema, 'https://raw.github.com/spidasoftware/schema/master/v1/calc/design.schema')
    it 'should validate and empty design', ->
      jsonString = fs.readFileSync("./examples/designs/empty_pole.json").toString()
      json = JSON.parse(jsonString)
      #errors = js.validate(json, designSchema)

    it 'check everything pole', ->
      jsonString = fs.readFileSync("./examples/designs/one_of_everything.json", "utf8")
      json = JSON.parse(jsonString)
      # errors = js.validate(json, designSchema)

    it 'check anchor guy pole', ->
      jsonString = fs.readFileSync("./examples/designs/anchor_guy.json", "utf8")
      json = JSON.parse(jsonString)
      # errors = js.validate(json, designSchema)

    it 'check bisector pole', ->
      jsonString = fs.readFileSync("./examples/designs/bisector.json", "utf8")
      json = JSON.parse(jsonString)
      # errors = js.validate(json, designSchema)

    it 'check insulator pole', ->
      jsonString = fs.readFileSync("./examples/designs/insulator.json", "utf8")
      json = JSON.parse(jsonString)
      # errors = js.validate(json, designSchema)

    it 'check wire pole', ->
      jsonString = fs.readFileSync("./examples/designs/wire.json", "utf8")
      json = JSON.parse(jsonString)
      # errors = js.validate(json, designSchema)

    it 'check crossarm pole', ->
      jsonString = fs.readFileSync("./examples/designs/xarm.json", "utf8")
      json = JSON.parse(jsonString)
      # errors = js.validate(json, designSchema)

  describe 'check the framing plan schema', ->
    js = new JaySchema()
    framingSchema = JSON.parse(fs.readFileSync "./v1/calc/framing_plan.schema")
    js.register(JSON.parse(fs.readFileSync "./v1/general/owner.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/owner.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/usage_group.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/usage_group.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/general/measurable.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/measurable.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/general/bearing.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/bearing.schema')
    js.register(framingSchema, 'https://raw.github.com/spidasoftware/schema/master/v1/calc/framing_plan.schema')

    it 'validate a simple framing plan schema', ->
      jsonString = fs.readFileSync("./examples/designs/simple_framing_plan.json").toString()
      json = JSON.parse(jsonString)
      errors = js.validate(json, framingSchema)
      expect(errors.length).toBe(0)

  describe 'check the calc project schema', ->
    js = new JaySchema()
    projectSchema = JSON.parse(fs.readFileSync "./v1/calc/calc_project.schema")
    js.register(JSON.parse(fs.readFileSync "./v1/general/address.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/address.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/general/bearing.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/bearing.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/general/owner.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/owner.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/pm/data_form.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/pm/data_form.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/general/measurable.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/general/measurable.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/usage_group.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/usage_group.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/point.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/point.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/environment.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/environment.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/attachment.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/attachment.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/damage_type.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/damage_type.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/client_pole_reference.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/client_pole_reference.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/client_equipment_reference.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/client_equipment_reference.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/client_wire_reference.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/client_wire_reference.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/wep_type.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/wep_type.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/framing_plan.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/framing_plan.schema')
    js.register(JSON.parse(fs.readFileSync "./v1/calc/design.schema"), 'https://raw.github.com/spidasoftware/schema/master/v1/calc/design.schema')
    js.register(projectSchema, 'https://raw.github.com/spidasoftware/schema/master/v1/calc/calc_project.schema')
     
    it 'should validate a minimal project schema', ->
      jsonString = fs.readFileSync("./examples/projects/minimal_project_with_gps.json").toString()
      json = JSON.parse(jsonString)
      # errors = js.validate(json, projectSchema)
      # expect(errors.length).toBe(0)

    it 'check full project schema', ->
      jsonString = fs.readFileSync("./examples/projects/full_project.json").toString()
      json = JSON.parse(jsonString)
      # errors = js.validate(json, projectSchema)
      # expect(errors.length).toBe(0)

    it 'check completely minimal project schema', ->
      jsonString = fs.readFileSync("./examples/projects/minimal_project_no_designs.json").toString()
      json = JSON.parse(jsonString)
      # errors = js.validate(json, projectSchema)
      # expect(errors.length).toBe(0)


