describe 'project', ->
  util = require('util')
  fs = require('fs')
  JSV = require("JSV").JSV
  env = JSV.createEnvironment()

  it 'load the external project schema', ->
    #Include some stuff.
    #Load schema
    data = fs.readFileSync "./v1/project/project.json"
    schema = JSON.parse(data)
    json = {"name":"test name"}
    report = env.validate(json, schema)
    expect(report.errors.length).toBe(0)
    if (report.errors.length != 0) 
      util.puts JSON.stringify(report.errors, null, 2)    
    