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

	it 'check framing plan schema', ->
		util.puts ("Checking basic plan")
		jsonString = fs.readFileSync("./examples/designs/simple_framing_plan.json").toString()
		json = JSON.parse(jsonString)
		framingSchema = "./public/v1/calc/framing_plan.schema"
		validate(json, framingSchema)