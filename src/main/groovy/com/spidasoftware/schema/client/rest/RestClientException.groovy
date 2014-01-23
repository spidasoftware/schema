package com.spidasoftware.schema.client.rest

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/17/14
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
class RestClientException extends RuntimeException {

	RestClientException(String message) {
		super(message)
	}

	RestClientException(String message, Throwable cause) {
		super(message, cause)
	}
}
