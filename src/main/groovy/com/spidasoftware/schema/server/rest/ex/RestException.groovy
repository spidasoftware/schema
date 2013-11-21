package com.spidasoftware.schema.server.rest.ex

import net.sf.json.*


abstract class RestException extends RuntimeException {
	abstract def code

	RestException(){}

	RestException(String message){
		super(message)
	}
	RestException(String message, Throwable cause){
		super(message, cause)
	}

	def toJSON(){
		def json = new JSONObject()
		json.put("status", "error")
		json.put("code", getCode())
		json.put("message", getMessage())
		return json
	}
}