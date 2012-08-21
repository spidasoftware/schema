describe 'project', ->
  it 'load the external project schema', ->
    #Include some stuff.
    util = require('util')
    fs = require('fs')
    JSV = require("JSV").JSV
    env = JSV.createEnvironment()
    #Load schema
    data = fs.readFileSync "./v1/project/project.json"
    schema = JSON.parse(data)
    json = {"name":"test name"}
    report = env.validate(json, schema)
    expect(report).not.toBe(report.errors.length==0)
    if (report.errors.length != 0) 
      util.puts JSON.stringify(report.errors, null, 2)    
    
    