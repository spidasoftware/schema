describe 'calc', ->

	#Include some stuff.
	util = require('util')
	fs = require('fs')
	JSV = require("JSV").JSV
	env = JSV.createEnvironment("json-schema-draft-03")
	path = require("path")
	designSchema = "./public/v1/calc/design.schema"

	loadSchemasInFolder = (folder) ->
		files = fs.readdirSync(folder)
		util.puts "loading schemas in #{folder}"
		for file in files
			do (file) ->
				if path.extname(file) is ".schema"
					util.puts "loading schema for file #{file}"
					schemaData = fs.readFileSync("#{folder}/#{file}")
					env.createSchema( schemaData, true, path.basename(file, ".schema")  )


	loadSupportSchemas = () ->
		loadSchemasInFolder("./v1/general")
		loadSchemasInFolder("./v1/calc")


	validate = (json, schemaFile, success=true) ->
		loadSupportSchemas()
		#util.puts(schemaFile )
		#util.puts(success)
		util.puts("json string to validate.")
		util.puts(JSON.stringify(json, null, 2))
		data = fs.readFileSync(schemaFile)
		schema = JSON.parse(data)
		report = env.validate(json, schema)
		util.puts "Errors: #{JSON.stringify(report.errors, null, 2)}"
		if success
			expect(report.errors.length).toBe(0)
		else
			expect(report.errors.length).not.toBe(0)

	it 'Check point schema', ->
		json = {
			"id": "anchor1",
			"uuid": "682db637-0f31-4847-9cdf-25ba9613a75c",
			"distance": {
				"value": 10,
				"unit": "FOOT"
			}
			"direction": 360
		}

		validate(json, "./v1/calc/point.schema", true)

	it 'check usage group schema', ->
		success = "PRIMARY"

		failure = "NONEXISTANT"
		testSchema = "./v1/calc/usage_group.schema"
		validate(success, testSchema, true)
		validate(failure, testSchema, false)

	it 'check empty design', ->
		util.puts ("Checking empty pole")
		jsonString = fs.readFileSync("./examples/designs/empty_pole.json").toString()
		json = JSON.parse(jsonString)
		validate(json, designSchema)

	it 'check everything pole', ->
		util.puts ("Checking pole with everything")
		jsonString = fs.readFileSync("./examples/designs/one_of_everything.json", "utf8")
		json = JSON.parse(jsonString)
		validate(json, designSchema)

	it 'check anchor guy pole', ->
		util.puts("Checking pole with anchor and guy")
		jsonString = fs.readFileSync("./examples/designs/anchor_guy.json", "utf8")
		json = JSON.parse(jsonString)
		validate(json, designSchema)

	it 'check bisector pole', ->
		util.puts("Checking pole with bisector guy")
		jsonString = fs.readFileSync("./examples/designs/bisector.json", "utf8")
		json = JSON.parse(jsonString)
		validate(json, designSchema)

	it 'check insulator pole', ->
		util.puts("Checking pole with insulator")
		jsonString = fs.readFileSync("./examples/designs/insulator.json", "utf8")
		json = JSON.parse(jsonString)
		validate(json, designSchema)

	it 'check wire pole', ->
		util.puts("Checking pole with wire")
		jsonString = fs.readFileSync("./examples/designs/wire.json", "utf8")
		json = JSON.parse(jsonString)
		validate(json, designSchema)

	it 'check crossarm pole', ->
		util.puts("Checking pole with crossarm.")
		jsonString = fs.readFileSync("./examples/designs/xarm.json", "utf8")
		json = JSON.parse(jsonString)
		validate(json, designSchema)

	it 'check framing plan schema', ->
		util.puts ("Checking basic plan")
		jsonString = fs.readFileSync("./examples/designs/simple_framing_plan.json").toString()
		json = JSON.parse(jsonString)
		framingSchema = "./public/v1/calc/framing_plan.schema"
		validate(json, framingSchema)

	it 'check minimal project schema', ->
		util.puts ("Checking minimal project")
		jsonString = fs.readFileSync("./examples/projects/minimal_project_with_gps.json").toString()
		json = JSON.parse(jsonString)
		framingSchema = "./public/v1/calc/calc_project.schema"
		validate(json, framingSchema)

	it 'check full project schema', ->
		util.puts ("Checking larger project")
		jsonString = fs.readFileSync("./examples/projects/full_project.json").toString()
		json = JSON.parse(jsonString)
		framingSchema = "./public/v1/calc/calc_project.schema"
		validate(json, framingSchema)

	it 'check completely minimal project schema', ->
		util.puts ("Checking larger project")
		jsonString = fs.readFileSync("./examples/projects/minimal_project_no_designs.json").toString()
		json = JSON.parse(jsonString)
		framingSchema = "./public/v1/calc/calc_project.schema"
		validate(json, framingSchema)

