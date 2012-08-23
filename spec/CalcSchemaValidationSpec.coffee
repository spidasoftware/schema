describe 'CalcSchemaValidation', ->
	#Include some stuff.
	util = require('util')
	fs = require('fs')
	JSV = require("JSV").JSV
	env = JSV.createEnvironment()
	schema = fs.readFileSync "./node_modules/JSV/schemas/json-schema-draft-03/schema.json"
	
	testSchema = (file) ->
		if (file.search(".DS_Store") is -1)
			util.puts("Testing #{file}")
			data = fs.readFileSync file
			if (data.length isnt 0)
				json = JSON.parse(data)		
				report = env.validate(json, schema)
				if (report.errors.length != 0) 
					util.puts JSON.stringify(report)
				expect(report).not.toBe(report.errors.length==0)


	it 'test calc schemas', ->
		util.puts("testing calc folder")
		calcFiles = fs.readdirSync("./v1/calc")
		util.puts(calcFiles)
		testSchema("./v1/calc/#{file}") for file in calcFiles
		

	it 'test general schemas', ->
		util.puts("testing general folder")
		generalFiles = fs.readdirSync("./v1/general")
		util.puts(generalFiles)
		testSchema("./v1/general/#{file}") for file in generalFiles


