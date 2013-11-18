package com.spidasoftware.schema.server.rest.ex



class MissingParamException extends RestException {
	def code = 400

	MissingParamException(String param){
		super("Missing required parameter: ${param}".toString())
	}


}