package com.spidasoftware.schema.server.rest.ex


class MissingResourceException extends RestException {
	def code = 404

	MissingResourceException(String id){
		super("No resource could be found with _id: ${id}".toString())
	}
}