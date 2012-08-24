describe 'calc', ->

	#Include some stuff.
	util = require('util')
	fs = require('fs')
	JSV = require("JSV").JSV
	env = JSV.createEnvironment("json-schema-draft-03")
	path = require("path")

	loadSchemasInFolder = (folder) ->
		files = fs.readdirSync(folder)
		util.puts "loading schemas in #{folder}"
		for file in files
			do (file) ->
				if path.extname(file) is ".json"
					util.puts "loading schema for file #{file}"
					schemaData = fs.readFileSync("#{folder}/#{file}")
					env.createSchema( schemaData, true, path.basename(file, ".json")  )


	loadSupportSchemas = () ->
		loadSchemasInFolder("./v1/general")
		loadSchemasInFolder("./v1/calc")


	validate = (json, schemaFile, success) ->
		loadSupportSchemas()
		util.puts(schemaFile )
		util.puts(success)
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

		validate(json, "./v1/calc/point.json", true)

	it 'check usage group schema', ->
		success =
		  id: "PRIMARY",
		  industry: "UTILITY"

		failure = {
			"id": "NONEXISTANT",
			"industry": "COMMUNICATION"
        }
		testSchema = "./v1/calc/usage_group.json"
		validate(success, testSchema, true)
		validate(failure, testSchema, false)

	it 'check design schema', ->
		json = fs.readFileSync("./examples/baseDesign.json")
		designSchema = "./v1/calc/design.json"
		validate(json, designSchema)








