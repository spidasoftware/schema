describe 'project', ->
  util = require('util')
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()
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
    loadSchemasInFolder("./v1/project")
    loadSchemasInFolder("./v1/asset")


  it 'load the external project schema', ->
    #Include some stuff.
    #Load schema
    loadSupportSchemas()
    data = fs.readFileSync "./v1/project/project.json"
    schema = JSON.parse(data)
    json = {"name":"test name"}
    report = env.validate(json, schema)
    expect(report.errors.length).toBe(0)
    if (report.errors.length != 0) 
      util.puts JSON.stringify(report.errors, null, 2)    
    
