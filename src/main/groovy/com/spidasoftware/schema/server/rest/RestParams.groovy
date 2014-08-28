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

				def paramId = it.id //id field always matches up with the key in the params map

				//We may use a different key internally for the query. If this is set, then use it
				//otherwise, just use the id as the key in the new params
				def newParamsKey = it.documentProperty ?: paramId

				if (it.required && !params[paramId]){
					//if a required param is not present, throw an exception
					log.info("Missing parameter ${paramId}")
					throw new MissingParamException(paramId)
				} else if (params[paramId]){
					log.debug("Adding param: $paramId")
					if (it.schema){
						//if there's a schema specified, then validate against it
						validateAgainstSchema(it.schema, params[paramId])
						newParams[newParamsKey] = JSONSerializer.toJSON(params[paramId])
					} else {
                        if(it.enum && !it.enum.contains(params[paramId].toString())) {
                            throw new InvalidParameterException(it.id + " with value: ${params[paramId]} is not valid:\nMust be one of ${it.enum}")
                        }
						newParams[newParamsKey] = params[paramId]
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
                        if(paramValue == null){
                            paramValue = it.defaultValue
                        }
						if(it.id == "limit") {
                            if(paramValue <= 0) {
                                paramValue = it.defaultValue
                            }
	                        def projectionMaxByType = method.projectionMaxByType?.getInt(newParams.format.toString())
	                        if(projectionMaxByType != null) {
                                paramValue = Math.min(paramValue, projectionMaxByType)
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