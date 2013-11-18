package com.spidasoftware.schema.server.rest.ex


class InvalidParameterException extends RestException {
	def code = 400

	InvalidParameterException(String param){
		super("Parameter: ${param} is invalid".toString())
	}
	
}