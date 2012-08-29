describe 'asset', ->
  util = require('util')
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()

  schema = JSON.parse(fs.readFileSync "./v1/general/rpc_method.schema")
   
  it 'load the rpc_asset file', ->  
    service = JSON.parse(fs.readFileSync "./v1/asset/rpc_asset.json")
    for method in service
      console.log ""+JSON.stringify(method, null, 2)
    report = env.validate(schema, service)
    expect(report.errors.length).toBe(0)
    if (report.errors.length != 0) 
      util.puts JSON.stringify(report.errors, null, 2)    