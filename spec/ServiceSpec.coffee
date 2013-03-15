describe 'asset', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  testUtils = require "./test_utils"


  schema = JSON.parse(fs.readFileSync "./v1/general/service_method.schema")
   
  it 'make sure the asset service descriptor validates', -> 
    logger.debug("make sure the asset service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/asset/interfaces/asset.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)

  it 'make sure the geo coder service descriptor validates', ->  
    logger.debug("make sure the geo coder service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/asset/interfaces/asset_search.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)

  it 'make sure the geo coder service descriptor validates', ->  
    logger.debug("make sure the geo coder service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/geo/interfaces/geo_coder.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)
   
  it 'make sure the user service descriptor validates', ->  
    logger.debug("make sure the user service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/user/interfaces/users.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)  

  it 'make sure the users security service descriptor validates', ->  
    logger.debug("make sure the users security service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/user/interfaces/users_security.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)

  it 'make sure the wire analysis descriptor validates', ->  
    logger.debug("make sure the wire analysis service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/calc/interfaces/wire_analysis.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)
  
  it 'make sure the client data descriptor validates', ->  
    logger.debug("make sure the client data service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/calc/interfaces/client_data.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)
   