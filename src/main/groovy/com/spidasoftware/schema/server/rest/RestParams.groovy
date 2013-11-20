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
					log.debug("Removing extra parameter: ${it}")
					it.remove()
				}
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
						newParams[key] = params[key]
					}
				}
			}

			//if the params are going to the list method, we'll have a special way of dealing with them
			if (actionName == "list"){
				def listParams = [:]
				def projectionParams = [:]

				api.projection.parameters.each{
					projectionParams["${it.id}"] = params["${it.id}"] ?: it.defaultValue
				}
				
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
			throw new InvalidParameterException(schemaPath.substring(schemaPath.lastIndexOf('/'), schemaPath.lastIndexOf('.schema')))
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