package com.spidasoftware.schema.server.rest


import net.sf.json.*
import com.spidasoftware.schema.server.rest.ex.*
import com.spidasoftware.schema.validation.Validator
import org.apache.log4j.Logger



class RestParams {
	private static final def log = Logger.getLogger(RestParams.class)
	def api

	def apiPath

	RestParams(){}

	RestParams(String pathToSchema){
		setApiPath(pathToSchema)
	}

	RestParams(JSONObject api){
		this.api = api
	}


	def validateAndFormat(resource, actionName, params) throws Exception {

		log.debug("RestUtils validating params for ${resource} -- ${actionName}...")
		try {
			def method = api.resources["${resource}"]["${actionName}"]
			if (!method){
				log.error("Could not find documentation for action: ${actionName} and resource: ${resource}")
				
				throw new UnsupportedOperationException("Could not find action: ${actionName} for resource: ${resource}")
			}
	

			//go through each of the method params and make sure it's valid
			def validParams = method.parameters.collect{it.id}
			def projectionParamNames = api.projection.parameters.collect{it.id}

			//if we're searching, then there's some additional parameters that are valid
			if (actionName == "list"){
				validParams.addAll(projectionParamNames)
			}
			log.debug("Valid parameters for ${actionName} are: ${validParams}")

			//remove all the params that aren't actually used for this operation
			params.each{
				if (!validParams.contains(it.key)){
					log.debug("Dropping extra parameter: ${it.key}")
				}
			}

			if(method.atLeastOneParamRequired != null && method.atLeastOneParamRequired.every { params[it] == null }) {
				throw new InvalidParameterException("At least one of ${method.atLeastOneParamRequired} must be specified")	
			}

            if(!params.format) {
                params["format"] = api.defaultFormat
            }

            def newParams = [:]
			//make sure that any required parameters are present
			method.parameters.each{
				log.debug("Checking parameter: ${it.id}")
				def key = it.id
				if (it.required && !params[key]){
					//if a required param is not present, throw an exception
					log.info("Missing parameter ${key}")
					throw new MissingParamException(key)
				} else if (params[key]){
					log.debug("Adding param: $key")
					if (it.schema){
						//if there's a schema specified, then validate against it
						validateAgainstSchema(it.schema, params[key])
						newParams[key] = JSONSerializer.toJSON(params[key])
					} else {
                        if(it.enum && !it.enum.contains(params[key].toString())) {
                            throw new InvalidParameterException(it.id + " with value: ${params[key]} is not valid:\nMust be one of ${it.enum}")
                        }
						newParams[key] = params[key]
					}
				}
			}

			//if the params are going to the list method, we'll have a special way of dealing with them
			if (actionName == "list"){
				def listParams = [:]
				def projectionParams = [:]

				api.projection.parameters.each{
					def clazz = it.defaultValue.class
					try {
						def paramValue = params["${it.id}"]?.asType(clazz)
						if(paramValue == null) {
							paramValue = it.defaultValue
						}
						if(it.id == "limit") {
	                        def projectionMaxByType = method.projectionMaxByType?.getInt(newParams.format.toString())
	                        if(projectionMaxByType != null) {
	                        	if(paramValue == 0) {
	                        		paramValue = projectionMaxByType
	                        	} else if(projectionMaxByType > 0) {
	                            	paramValue = Math.min(paramValue, projectionMaxByType)
	                            }
	                        }
	                    }
						projectionParams["${it.id}"] = paramValue
					} catch (NumberFormatException e){
						throw new InvalidParameterException(it.id)
					}
					
				}
                listParams.put("format", newParams.format)
				newParams.remove("format")
				listParams.put("query", newParams)
				listParams.put("projection", projectionParams)
				
				return listParams
			} else {

				return newParams
			}
			
		} catch (JSONException e){
			log.error(e, e)
			throw new InvalidParameterException("invalid_json")
		} 
	}


	def validateAgainstSchema = { schemaPath, param ->
		log.debug("Validating param against ${schemaPath}")
		
		def report = new Validator().validateAndReport(schemaPath, param)

		if (!report){
			throw new InvalidParameterException("invalid_json")
		} else if (!report.isSuccess()){
			def type = schemaPath.substring(schemaPath.lastIndexOf('/'), schemaPath.lastIndexOf('.schema'))
			log.warn("$type is not valid against the schema; \n" + report.toString())
			throw new InvalidParameterException(type + " is not valid:\n${report.toString()}")
		}
	}



	void setApiPath(String pathToSchema){
		def text = getClass().getResourceAsStream(pathToSchema).getText()
		api = JSONObject.fromObject(text)
		this.apiPath = pathToSchema
	}

	String getApiPath(){
		return this.apiPath
	}
}