package com.spidasoftware.schema.server.rest.ex

class ForbiddenException extends RestException {
	def code = 403

	ForbiddenException(String message){
		super(message)
	}
}