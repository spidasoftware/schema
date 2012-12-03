describe 'asset', ->
  log4js = require('log4js')
  logger = log4js.getLogger()
  fs = require('fs')
  testUtils = require "./test_utils"

  #testUtils.supportedSchemas(["./v1/general", "./v1/asset", "./v1/geo", "./v1/user"])

  schema = JSON.parse(fs.readFileSync "./v1/general/service_method.schema")
   
  it 'make sure the asset service descriptor validates', -> 
    logger.info("make sure the asset service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/asset/asset_service.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)

  it 'make sure the geo coder service descriptor validates', ->  
    logger.info("make sure the geo coder service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/asset/asset_search_service.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)

  it 'make sure the geo coder service descriptor validates', ->  
    logger.info("make sure the geo coder service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/geo/geo_coder_service.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)
   
  it 'make sure the user service descriptor validates', ->  
    logger.info("make sure the user service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/user/users_service.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)  

  it 'make sure the users security service descriptor validates', ->  
    logger.info("make sure the users security service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/user/users_security_service.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)

  it 'make sure the wire analysis descriptor validates', ->  
    logger.info("make sure the wire analysis service descriptor validates") 
    service = JSON.parse(fs.readFileSync "./v1/calc/analysis/wire_analysis_service.json")
    for method of service
      if method!="id" and method!="description" 
        testUtils.validate(service[method], "./v1/general/service_method.schema", true)
   