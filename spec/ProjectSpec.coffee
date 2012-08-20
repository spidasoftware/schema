describe 'project', ->
  it 'load the external schema', ->
    #Include some stuff.
    util = require('util')
    fs = require('fs')
    JSV = require("JSV").JSV
    #Load schema
    projectSchema = null
    fs.readFile("./v1/project/project.json", (err, data)->
      projectSchema = JSON.parse(data)
      json = {Jagger:"Rock"}
      schema = {"type" : "object"}
      env = JSV.createEnvironment()
      report = env.validate(json, schema)
      if (report.errors.length == 0) 
        util.puts projectSchema
        util.puts "Woot"
    )
    
    