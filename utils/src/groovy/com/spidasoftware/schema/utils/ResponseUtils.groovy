package com.spidasoftware.schema.utils

import net.sf.json.JSONObject;

class ResponseUtils {

	static String result(resultJson){
		return new JSONObject(result:resultJson).toString()
	}

	static String error(codeString, messageString){
		return new JSONObject(error:new JSONObject(code:codeString, message:messageString)).toString()
	}

	static String missingParam(paramName){
		return new JSONObject(error:new JSONObject(code:"MISSING_REQUIRED_PARAM", message:"Please provide the '$paramName' parameter.")).toString()
	}

	static String invalidParam(paramName){
		return new JSONObject(error:new JSONObject(code:"INVALID_PARAM", message:"Please correct the '$paramName' parameter.")).toString()
	}

	static String notImplemented(){
		return new JSONObject(error:new JSONObject(code:"INTERNAL_ERROR", message:"Method not yet implemented.")).toString()
	}

}