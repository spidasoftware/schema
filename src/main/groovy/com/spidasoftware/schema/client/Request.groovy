package com.spidasoftware.schema.client

import org.apache.http.client.config.RequestConfig
import org.apache.http.protocol.HttpContext
import org.apache.http.HttpEntity

/**
 * Holds settings for an http client interface
 *
 * Required Fields:
 *
 * 		httpMethod: specify the http method to use: GET, POST, PUT, DELETE, etc.
 *
 * 		uri: base uri to make the request to, parameters will automatically be added if appropriate
 *
 * Not Required:
 *
 * 		parameters: a map of params that will get added to the request Entity (for POST or PUT), or
 * 		     added to the URI (any other method). These will automatically be escaped as needed. may be null
 *
 * 		headers: a map of request headers to use for this request. May be null
 *
 * 		config: config for a specific request
 *
 * 		httpContext: context for a specific request - can be used to set credentials for one request
 *
 * 		responseHandler: an optional Closure that get's called after the response has been received. The default will
 * 		     simply return the response body as a String, or throw an HttpResponseException if the status code is >= 300.
 * 		     Note that the default behavior is for GenericHttpClient to call consumeContent() on the entity AFTER this closure
 * 		     returns. It will then release the requests connection and then return the result of the responseHandler Closure.
 *
 * 		keepStreamOpen: should the response input stream be left open (defaults to closing the stream)
 *
 * Created by jeremy on 5/16/14.
 */
class Request {

	String httpMethod 					//required
	URI uri 							//required
	Map<String, Object> parameters 		//not required
	Map<String, String> headers 		//not required
	RequestConfig config 				//not required
	HttpContext httpContext 			//not required
	Closure responseHandler				//not required
	boolean keepStreamOpen = false		//not required
	HttpEntity customEntity         //not required
}

