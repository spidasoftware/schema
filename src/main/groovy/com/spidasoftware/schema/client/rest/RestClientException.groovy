package com.spidasoftware.schema.client.rest

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/17/14
 * Time: 2:55 PM
 */
class RestClientException extends RuntimeException {

	RestClientException(String message) {
		super(message)
	}

	RestClientException(String message, Throwable cause) {
		super(message, cause)
	}
}
