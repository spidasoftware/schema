describe 'client', ->

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

	validate = (jsonFile, schemaFile, success=true) ->
		#loadSupportSchemas()
		util.puts("jsonFile #{jsonFile}")
		jsonData = fs.readFileSync(jsonFile)
		util.puts("jsonData: #{jsonData.toString()}")
		json = JSON.parse(jsonData)
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

	it 'check anchor', ->
		validate("./examples/client/client_anchor_example.json", "./public/v1/calc/client/client_anchor.schema")

	it 'check crossarm', ->
		validate("./examples/client/client_xarm_example.json", "./public/v1/calc/client/client_crossarm.schema")

	it 'check equipment', ->
		validate("./examples/client/client_equipment_example.json", "./public/v1/calc/client/client_equipment.schema")

	it 'check insulator', ->
		validate("./examples/client/client_insulator_example.json", "./public/v1/calc/client/client_insulator.schema")

	it 'check pole', ->
		validate("./examples/client/client_pole_example.json", "./public/v1/calc/client/client_pole.schema")

	it 'check wire', ->
		validate("./examples/client/client_wire_example.json", "./public/v1/calc/client/client_wire.schema")
