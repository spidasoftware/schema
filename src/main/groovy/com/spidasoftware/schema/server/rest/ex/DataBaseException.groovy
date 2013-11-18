package com.spidasoftware.schema.server.rest.ex

class DataBaseException extends RestException {
	def code = 500

	DataBaseException(message){
		super(message.toString())
	}

	DataBaseException(message, cause){
		super(message.toString(), cause)
	}
	
}