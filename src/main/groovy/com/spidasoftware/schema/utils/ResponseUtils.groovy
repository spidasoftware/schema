package com.spidasoftware.schema.utils

import net.sf.json.JSONObject
import net.sf.json.JSONArray

class ResponseUtils {

	static String result(resultJsonString){
		return new JSONObject(result:resultJsonString).toString()
	}

	static String resultId(id){
		return new JSONObject(result:new JSONObject(id:id)).toString()
	}

	static String resultIds(ids){
		def result = new JSONArray()
		result.addAll(ids)
		return new JSONObject(result:result).toString()
	}

	static String error(codeString, messageString){
		return new JSONObject(error:new JSONObject(code:codeString, message:messageString.toString())).toString()
	}

	static String missingParam(paramName){
		return new JSONObject(error:new JSONObject(code:"MISSING_REQUIRED_PARAM", message:"Please provide the ${paramName.toString()} parameter.".toString())).toString()
	}

	static String missingParamMsg(messageString){
		return new JSONObject(error:new JSONObject(code:"MISSING_REQUIRED_PARAM", message:messageString.toString())).toString()
	}

	static String invalidParam(paramName){
		return new JSONObject(error:new JSONObject(code:"INVALID_PARAM", message:"Please correct the ${paramName.toString()} parameter.".toString())).toString()
	}

	static String invalidParamMsg(messageString){
		return new JSONObject(error:new JSONObject(code:"INVALID_PARAM", message:messageString.toString())).toString()
	}

	static String missingResource(resource){
		return new JSONObject(error:new JSONObject(code:"MISSING_RESOURCE", message:"The requested ${resource.toString()} was not found.".toString())).toString()
	}

	static String internalError(message){
		return new JSONObject(error:new JSONObject(code:"INTERNAL_ERROR", message:message.toString())).toString()
	}

	static String permissionDenied(message){
		return new JSONObject(error:new JSONObject(code:"PERMISSION_DENIED", message:message.toString())).toString()
	}

	static String notImplemented(){
		return new JSONObject(error:new JSONObject(code:"INTERNAL_ERROR", message:"Method not yet implemented.".toString())).toString()
	}

}