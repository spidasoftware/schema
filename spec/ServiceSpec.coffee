describe 'asset', ->
  util = require('util')
  fs = require('fs')
  JSV = require("JSV").JSV
  path = require("path")
  env = JSV.createEnvironment()

  loadSchemasInFolder = (folder) ->
    files = fs.readdirSync(folder)
    util.debug "loading schemas in #{folder}"
    for file in files
      do (file) ->
        if path.extname(file) is ".schema"
          util.debug "loading schema for file #{file}"
          schemaData = fs.readFileSync("#{folder}/#{file}")
          env.createSchema( schemaData, true, path.basename(file, ".schema")  )


  loadSupportSchemas = () ->
    loadSchemasInFolder("./v1/general")
    loadSchemasInFolder("./v1/asset")


  validate = (json, schemaFile, success=true) ->
    loadSupportSchemas()
    util.debug("json string to validate.")
    util.debug(JSON.stringify(json, null, 2))
    data = fs.readFileSync(schemaFile)
    schema = JSON.parse(data)
    report = env.validate(json, schema)
    util.log "Errors: #{JSON.stringify(report.errors, null, 2)}"
    if success
      expect(report.errors.length).toBe(0)
    else
      expect(report.errors.length).not.toBe(0)

  schema = JSON.parse(fs.readFileSync "./v1/general/rpc_method.schema")
   
  it 'make sure the asset service descriptor validates', ->  
    service = JSON.parse(fs.readFileSync "./v1/asset/rpc_asset.json")
    for method of service
      if method!="id" and method!="description" 
        validate(service[method], "./v1/general/rpc_method.schema", true)

  it 'make sure the asset search service descriptor validates', ->  
    service = JSON.parse(fs.readFileSync "./v1/asset/rpc_asset.json")
    for method of service
      if method!="id" and method!="description" 
        validate(service[method], "./v1/general/rpc_method.schema", true)
   