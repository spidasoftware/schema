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



	it 'Check point schema', ->
		#Load schema
		loadSupportSchemas()
		data = fs.readFileSync "./v1/calc/point.json"
		schema = JSON.parse(data)
		json = {
			"id": "anchor1",
			"uuid": "682db637-0f31-4847-9cdf-25ba9613a75c",
			"distance": {
				"value": "10",
				"unit": "FOOT"
			},
			"direction": "360"
		}


		report = env.validate(json, schema)
		if (report.errors.length != 0)
		  util.puts JSON.stringify(report.errors, null, 2)
		expect(report).not.toBe(report.errors.length==0)



